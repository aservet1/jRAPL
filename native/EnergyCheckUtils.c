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

#include "EnergyCheckUtils.h"
#include "ArchSpec.h"
#include "MSR.h"
#include "EnergyStats.h"

#define MSR_DRAM_ENERGY_UNIT 0.000015

static rapl_msr_parameter* parameters = NULL;
static int* msr_fds = NULL;

static int power_domains_supported;
static uint32_t cpu_model;
static rapl_msr_unit rapl_unit;
static uint64_t num_pkg;
static int wraparound_energy = -1;
static int num_cores;

// only valid after ProfileInit() has been called. otherwise ignore it
int* get_msr_fds() {
	return msr_fds;
}

static inline double read_pkg(int i) {
	double result = read_msr(msr_fds[i], MSR_PKG_ENERGY_STATUS);	//First 32 bits so don't need shift bits.
	return (double) result * rapl_unit.energy;
}
static inline double read_core(int i) {
	double result = read_msr(msr_fds[i], MSR_PP0_ENERGY_STATUS);
	return (double) result * rapl_unit.energy;
}
static inline double read_gpu(int i) {
	double result = read_msr(msr_fds[i],MSR_PP1_ENERGY_STATUS);
	return (double) result * rapl_unit.energy;
}
static inline double read_dram(int i) {
	double result = read_msr(msr_fds[i],MSR_DRAM_ENERGY_STATUS);
	if (cpu_model == BROADWELL || cpu_model == BROADWELL2) {
		return (double) result * MSR_DRAM_ENERGY_UNIT;
	} else {
		return (double) result * rapl_unit.energy;
	}
}

void ProfileInit() {
	int i;
	char msr_filename[BUFSIZ];
	int core = 0;

	num_pkg = getSocketNum(); 
	cpu_model = get_cpu_model();
	power_domains_supported = get_power_domains_supported(cpu_model,NULL);
	uint64_t num_pkg_thread = get_num_pkg_thread();

	/*only two domains are supported for parameters check*/
	parameters = (rapl_msr_parameter *)malloc(2 * sizeof(rapl_msr_parameter));
	msr_fds = (int *) malloc(num_pkg * sizeof(int));
	// printf("num_pkg: %d\nnum_pkg_thread: %d", num_pkg, num_pkg_thread);
	for(i = 0; i < num_pkg; i++) {
		if(i > 0) {
			core += num_pkg_thread / 2; 	//measure the first core of each package
		}
		sprintf(msr_filename, "/dev/cpu/%d/msr", core);
		msr_fds[i] = open(msr_filename, O_RDWR);
	}

	rapl_unit = get_rapl_unit(msr_fds[0]);
	wraparound_energy = get_wraparound_energy(rapl_unit.energy);
}


void ProfileDealloc() {
	for (int i = 0; i < num_pkg; i++) {
		close(msr_fds[i]);
	} free(msr_fds); msr_fds = NULL;
	free(parameters); parameters = NULL;
}

void EnergyStatCheck(EnergyStats stats_per_socket[num_pkg]) {
	struct timeval timestamp;

	for (int i = 0; i < num_pkg; i++) {
		int socket = i+1;
		stats_per_socket[i].socket = socket;
		stats_per_socket[i].pkg = read_pkg(i);
		stats_per_socket[i].core = read_core(i);

		switch(power_domains_supported) {
			case READ_FROM_DRAM_AND_GPU:
				stats_per_socket[i].dram = read_dram(i);
				stats_per_socket[i].gpu = read_gpu(i);
				break;

			case READ_FROM_DRAM:
				stats_per_socket[i].dram = read_dram(i);
				stats_per_socket[i].gpu = -1;
				break;

			case READ_FROM_GPU:
				stats_per_socket[i].dram = -1;
				stats_per_socket[i].gpu = read_gpu(i);
				break;

			case UNDEFINED_ARCHITECTURE:
				fprintf(stderr,"ERROR: Architecture not found: %X\n",cpu_model);
				break;
		}
		gettimeofday(&timestamp,NULL);
		stats_per_socket[i].timestamp = timestamp;
	}
}

void ProfileInitAllCores(int num_readings) {
	int i;
	char msr_filename[BUFSIZ];
	// int core = 0;

	num_pkg = getSocketNum(); 
	cpu_model = get_cpu_model();
	power_domains_supported = get_power_domains_supported(cpu_model,NULL);
	uint64_t num_pkg_thread = get_num_pkg_thread();

	/*only two domains are supported for parameters check*/
	parameters = (rapl_msr_parameter *)malloc(2 * sizeof(rapl_msr_parameter));
	num_cores = num_pkg*num_pkg_thread;
	msr_fds = (int *) malloc(num_pkg * sizeof(int) * num_cores);
	
	for(i = 0; i < num_cores; i++) {
		// if(i > 0) {
		// 	core += num_pkg_thread / 2; 	//measure the first core of each package
		// }
		sprintf(msr_filename, "/dev/cpu/%d/msr", i);
		printf("%s\n",msr_filename);
		msr_fds[i] = open(msr_filename, O_RDWR);
	}

	rapl_unit = get_rapl_unit(msr_fds[0]);
	wraparound_energy = get_wraparound_energy(rapl_unit.energy);
	for(int _ = 0; _ < num_readings; _++) {
		double pkg[num_cores];
		double dram[num_cores];
		double core[num_cores];
		double gpu[num_cores];
		for(int i = 0; i < num_cores; i++) {
			pkg[i]  =  read_pkg(i);
			dram[i] =  read_dram(i);
			core[i] =  read_core(i);
			gpu[i]  =  read_gpu(i);
		}
		for (int c = 0; c < num_cores; c++) {
			printf("%d || pkg: %f dram: %f gpu: %f core: %f\n", c, pkg[c], dram[c], gpu[c], core[c]);
		} printf("----------------------\n");
		sleep(1);
	}
}

void ProfileDeallocAllCores() {
	for (int i = 0; i < num_cores; i++) {
		close(msr_fds[i]);
	} free(msr_fds); msr_fds = NULL;
	free(parameters); parameters = NULL;
}
