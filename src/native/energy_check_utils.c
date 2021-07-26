#include <stdio.h>
#include <jni.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
#include <stdint.h>
#include <string.h>
#include <inttypes.h>
#include <sys/types.h>

#include "energy_check_utils.h"
#include "arch_spec.h"
#include "msr.h"
#include "energy_stats.h"
#include "utils.h"

#define MSR_DRAM_ENERGY_UNIT 0.000015

static rapl_msr_parameter* parameters = NULL; // what is a rapl_msr_parameter?
static int* msr_fds = NULL;

static int power_domains_supported;
static uint32_t micro_architecture;
static rapl_msr_unit rapl_unit;
static uint64_t num_sockets;
static double wraparound_energy = -1; // TODO wait, is this not even being used here?

// only valid after ProfileInit() has been called. it's set to NULL in all other cases
int*
get_msr_fds() {
	return msr_fds;
}

static inline float
read_pkg(int i) {
	uint32_t result = read_msr(msr_fds[i], MSR_PKG_ENERGY_STATUS);	//First 32 bits so don't need shift bits.
	return result * rapl_unit.energy;
}
static inline float
read_core(int i) {
	uint32_t result = read_msr(msr_fds[i], MSR_PP0_ENERGY_STATUS);
	return result * rapl_unit.energy;
}
static inline float
read_gpu(int i) {
	uint32_t result = read_msr(msr_fds[i],MSR_PP1_ENERGY_STATUS);
	return result * rapl_unit.energy;
}
static inline float
read_dram(int i) {
	uint32_t result = read_msr(msr_fds[i],MSR_DRAM_ENERGY_STATUS);
	if (micro_architecture == BROADWELL || micro_architecture == BROADWELL2) {
		return result * MSR_DRAM_ENERGY_UNIT;
	} else {
		return result * rapl_unit.energy;
	}
}

void
ProfileInit() {
	char msr_filename[BUFSIZ];
	int core = 0;

	num_sockets = getSocketNum(); 
	micro_architecture = get_micro_architecture();
	power_domains_supported = get_power_domains_supported(micro_architecture);
	uint64_t num_pkg_thread = get_num_pkg_thread();

	/*only two domains are supported for parameters check*/
	parameters = (rapl_msr_parameter *)malloc(2 * sizeof(rapl_msr_parameter));
	msr_fds = (int *) malloc(num_sockets * sizeof(int));

	for(int i = 0; i < num_sockets; i++) {
		if(i > 0) {
			core += num_pkg_thread / 2; 	//measure the first core of each package
		}
		sprintf(msr_filename, "/dev/cpu/%d/msr", core);
		msr_fds[i] = open(msr_filename, O_RDWR);
	}

	rapl_unit = get_rapl_unit(msr_fds[0]);
	wraparound_energy = get_wraparound_energy(rapl_unit.energy);
}

void
ProfileDealloc() {
	for (int i = 0; i < num_sockets; i++)
		close(msr_fds[i]);
	free(msr_fds); msr_fds = NULL;
	free(parameters); parameters = NULL;
}

void
EnergyStatCheck(EnergyStats stats_per_socket[num_sockets]) {
	switch(power_domains_supported) {
		case DRAM_GPU_CORE_PKG:
			for (int i = 0; i < num_sockets; i++) {
				stats_per_socket[i].dram = read_dram(i);
				stats_per_socket[i].gpu = read_gpu(i);
				stats_per_socket[i].core = read_core(i);
				stats_per_socket[i].pkg = read_pkg(i);
				stats_per_socket[i].timestamp = usec_since_epoch();
			} break;

		case DRAM_CORE_PKG:
			for (int i = 0; i < num_sockets; i++) {
				stats_per_socket[i].dram = read_dram(i);
				stats_per_socket[i].gpu = -1;
				stats_per_socket[i].core = read_core(i);
				stats_per_socket[i].pkg = read_pkg(i);
				stats_per_socket[i].timestamp = usec_since_epoch();
			} break;

		case GPU_CORE_PKG:
			for (int i = 0; i < num_sockets; i++) {
				stats_per_socket[i].dram = -1;
				stats_per_socket[i].gpu = read_gpu(i);
				stats_per_socket[i].core = read_core(i);
				stats_per_socket[i].pkg = read_pkg(i);
				stats_per_socket[i].timestamp = usec_since_epoch();
			} break;

		case UNDEFINED_ARCHITECTURE:
			fprintf(stderr,"ERROR: MicroArchitecture not supported: %X\n", micro_architecture);
			for (int i = 0; i < num_sockets; i++) {
				stats_per_socket[i].dram = -1;
				stats_per_socket[i].gpu = -1;
				stats_per_socket[i].core = -1;
				stats_per_socket[i].pkg = -1;
				stats_per_socket[i].timestamp = usec_since_epoch();
			} break;
			break;
	}
}
