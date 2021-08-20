#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
#include <stdint.h>
#include <string.h>
#include <inttypes.h>
#include <sys/types.h>
#include <assert.h>

#include "energy_check_utils.h"
#include "arch_spec.h"
#include "msr.h"
#include "utils.h"

#define MSR_DRAM_ENERGY_UNIT 0.000015

static rapl_msr_parameter* parameters = NULL; // what is a rapl_msr_parameter?
static int* msr_fds = NULL;

static int power_domains_supported;
static uint32_t micro_architecture;
static rapl_msr_unit rapl_unit;
static uint64_t num_sockets;
static double wraparound_energy = -1; // TODO wait, is this not even being used here?

int*
get_msr_fds() { // only valid after ProfileInit() has been called. it's set to NULL in all other cases
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
			} return;

		case DRAM_CORE_PKG:
			for (int i = 0; i < num_sockets; i++) {
				stats_per_socket[i].dram = read_dram(i);
				stats_per_socket[i].gpu = -1;
				stats_per_socket[i].core = read_core(i);
				stats_per_socket[i].pkg = read_pkg(i);
				stats_per_socket[i].timestamp = usec_since_epoch();
			} return;

		case GPU_CORE_PKG:
			for (int i = 0; i < num_sockets; i++) {
				stats_per_socket[i].dram = -1;
				stats_per_socket[i].gpu = read_gpu(i);
				stats_per_socket[i].core = read_core(i);
				stats_per_socket[i].pkg = read_pkg(i);
				stats_per_socket[i].timestamp = usec_since_epoch();
			} return;

		case UNDEFINED_ARCHITECTURE:
			fprintf(stderr,"ERROR: MicroArchitecture not supported: %X\n", micro_architecture);
			for (int i = 0; i < num_sockets; i++) {
				stats_per_socket[i].dram = -1;
				stats_per_socket[i].gpu = -1;
				stats_per_socket[i].core = -1;
				stats_per_socket[i].pkg = -1;
				stats_per_socket[i].timestamp = usec_since_epoch();
			} return;
	}
}

EnergyStats
energy_stats_subtract(EnergyStats x, EnergyStats y) {
	EnergyStats diff; //@TODO -- implement the wraparound for negative values
	diff.dram = (x.dram != -1 && y.dram != -1) ? x.dram - y.dram : -1;
	diff.core = (x.core != -1 && y.core != -1) ? x.core - y.core : -1;
	diff.gpu  = (x.gpu  != -1 && y.gpu  != -1) ? x.gpu  - y.gpu  : -1;
	diff.pkg  = (x.pkg  != -1 && y.pkg  != -1) ? x.pkg  - y.pkg  : -1;
	diff.timestamp = x.timestamp - y.timestamp;	
	return diff;
}

void
energy_stats_csv_header(char csv_header[512]) { 
	int offset = 0;
	const char* format;
	switch(power_domains_supported) {
		case DRAM_GPU_CORE_PKG:
			format = "dram_socket%d,gpu_socket%d,core_socket%d,pkg_socket%d,";
			for (int s = 1; s <= num_sockets; s++)
				offset += sprintf(csv_header + offset, format, s,s,s,s);
			sprintf(csv_header + offset, "timestamp");
			return;

		case DRAM_CORE_PKG:
			format = "dram_socket%d,core_socket%d,pkg_socket%d,";
			for (int s = 1; s <= num_sockets; s++)
				offset += sprintf(csv_header + offset, format, s,s,s);
			sprintf(csv_header + offset, "timestamp");
			return;

		case GPU_CORE_PKG:
			format = "gpu_socket_%d,core_socket%d,pkg_socket%d,";
			for (int s = 1; s <= num_sockets; s++)
				offset += sprintf(csv_header + offset, format, s,s,s);
			sprintf(csv_header + offset, "timestamp");
			return;

		default:
			sprintf(csv_header, "undefined_architecture");
			return;
	}
}

void
energy_stats_csv_string(EnergyStats estats[], char* csv_string) {
	int offset = 0;
	for (int i = 0; i < num_sockets; i++) {
		switch (power_domains_supported) {
			case DRAM_GPU_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.6f,%.6f,%.6f,%.6f,",
					estats[i].dram,
					estats[i].gpu,
					estats[i].core,
					estats[i].pkg
				);
				break;
			case GPU_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.6f,%.6f,%.6f,",
					estats[i].gpu,
					estats[i].core,
					estats[i].pkg
				);
				break;
			case DRAM_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.6f,%.6f,%.6f,",
					estats[i].dram,
					estats[i].core,
					estats[i].pkg
				);
				break;
			default:
				assert(0 && "error occurred in energy_stats_csv_string");
		}
	}
	sprintf(csv_string+offset, "%ld", estats[0].timestamp);
}
