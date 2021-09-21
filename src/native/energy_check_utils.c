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
EnergyStatCheck(energy_info_t energy_stat_per_socket[num_sockets]) {
	switch(power_domains_supported) {
		case DRAM_GPU_CORE_PKG:
			for (int i = 0; i < num_sockets; i++) {
				energy_stat_per_socket[i].time = usec_since_epoch();
				energy_stat_per_socket[i].dram = read_dram(i);
				energy_stat_per_socket[i].core = read_core(i);
				energy_stat_per_socket[i].gpu  = read_gpu(i);
				energy_stat_per_socket[i].pkg  = read_pkg(i);
			} return;
		case DRAM_CORE_PKG:
			for (int i = 0; i < num_sockets; i++) {
				energy_stat_per_socket[i].time = usec_since_epoch();
				energy_stat_per_socket[i].dram = read_dram(i);
				energy_stat_per_socket[i].core = read_core(i);
				energy_stat_per_socket[i].pkg  = read_pkg(i);
				energy_stat_per_socket[i].gpu  = -1;
			} return;
		case GPU_CORE_PKG:
			for (int i = 0; i < num_sockets; i++) {
				energy_stat_per_socket[i].time = usec_since_epoch();
				energy_stat_per_socket[i].dram = -1;
				energy_stat_per_socket[i].core = read_core(i);
				energy_stat_per_socket[i].pkg  = read_pkg(i);
				energy_stat_per_socket[i].gpu  = read_gpu(i);
			} return;
		case UNDEFINED_ARCHITECTURE:
			fprintf(stderr,"ERROR: MicroArchitecture not supported: %X\n", micro_architecture);
			for (int i = 0; i < num_sockets; i++) {
				energy_stat_per_socket[i].time = usec_since_epoch();
				energy_stat_per_socket[i].dram = -1;
				energy_stat_per_socket[i].core = -1;
				energy_stat_per_socket[i].pkg  = -1;
				energy_stat_per_socket[i].gpu  = -1;
			} return;
	}
}

energy_info_t
subtract_energy_stat(energy_info_t a, energy_info_t b) {
	assert(wraparound_energy != -1);

	energy_info_t diff;
	diff.dram = -1; diff.core = -1; diff.gpu = -1; diff.pkg  = -1;

	if (a.dram != -1 && b.dram != -1) {
		diff.dram = a.dram - b.dram;
		if (diff.dram < 0) {
			diff.dram += wraparound_energy;
		}
	} if (a.core != -1 && b.core != -1) {
		diff.core = a.core - b.core;
		if (diff.core < 0) {
			diff.core += wraparound_energy;
		}
	} if (a.gpu != -1 && b.gpu != -1) {
		diff.gpu  = a.gpu  - b.gpu;
		if (diff.gpu < 0) {
			diff.gpu += wraparound_energy;
		}
	} if (a.pkg != -1 && b.pkg != -1) {
		diff.pkg  = a.pkg  - b.pkg;
		if (diff.pkg < 0) {
			diff.pkg += wraparound_energy;
		}
	}

	diff.time =  a.time - b.time;

	return diff;
}

static void
energy_info_csv_header(char csv_header[512], char* time_value_column_label) { 
	int offset = 0;
	const char* format;
	switch(power_domains_supported) {
		case DRAM_GPU_CORE_PKG:
			format = "dram_socket%d,gpu_socket%d,core_socket%d,pkg_socket%d,";
			for (int s = 1; s <= num_sockets; s++)
				offset += sprintf(csv_header + offset, format, s,s,s,s);
			sprintf(csv_header + offset, "%s", time_value_column_label);
			return;
		case DRAM_CORE_PKG:
			format = "dram_socket%d,core_socket%d,pkg_socket%d,";
			for (int s = 1; s <= num_sockets; s++)
				offset += sprintf(csv_header + offset, format, s,s,s);
			sprintf(csv_header + offset, "%s", time_value_column_label);
			return;
		case GPU_CORE_PKG:
			format = "gpu_socket_%d,core_socket%d,pkg_socket%d,";
			for (int s = 1; s <= num_sockets; s++)
				offset += sprintf(csv_header + offset, format, s,s,s);
			sprintf(csv_header + offset, "%s", time_value_column_label);
			return;
		default:
			sprintf(csv_header, "undefined_architecture");
			return;
	}
}
void energy_diff_csv_header(char csv_header[512]) { energy_info_csv_header(csv_header, "elapsed_time"); }
void energy_stat_csv_header(char csv_header[512]) { energy_info_csv_header(csv_header,  "timestamp" );  }

void
energy_info_csv_string(energy_info_t energy_info_per_socket[], char* csv_string) {
	int offset = 0;
	for (int i = 0; i < num_sockets; i++) {
		switch (power_domains_supported) {
			case DRAM_GPU_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.6f,%.6f,%.6f,%.6f,",
					energy_info_per_socket[i].dram,
					energy_info_per_socket[i].gpu,
					energy_info_per_socket[i].core,
					energy_info_per_socket[i].pkg
				);
				break;
			case GPU_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.6f,%.6f,%.6f,",
					energy_info_per_socket[i].gpu,
					energy_info_per_socket[i].core,
					energy_info_per_socket[i].pkg
				);
				break;
			case DRAM_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.6f,%.6f,%.6f,",
					energy_info_per_socket[i].dram,
					energy_info_per_socket[i].core,
					energy_info_per_socket[i].pkg
				);
				break;
			default:
				assert(0 && "error occurred in energy_stats_csv_string");
		}
	}
	sprintf(csv_string+offset, "%ld", energy_info_per_socket[0].time);
}
