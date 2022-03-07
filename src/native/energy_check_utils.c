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
#include <stdbool.h>

#include "energy_check_utils.h"
#include "arch_spec.h"
#include "msr.h"
#include "utils.h"
#include "platform_support.h"

static rapl_msr_parameter* parameters = NULL; // this is for powercap RAPL interface, not energy status RAPL interface
static int* msr_fds = NULL;

static power_domain_support_info_t power_domains_supported;
static uint32_t micro_architecture_id;
static rapl_msr_unit rapl_unit;
static uint64_t num_sockets;
static double rapl_wraparound = 0;
static double dram_rapl_wraparound = 0;
static double dram_energy_unit = 1;
static double energy_unit = 1;

int*
get_msr_fds() { // only valid after ProfileInit() has been called. it's set to NULL in all other cases
	return msr_fds;
}

static inline float
read_PKG(int socket) {
	uint32_t msr_info = read_msr(msr_fds[socket], MSR_PKG_ENERGY_STATUS);	//First 32 bits so don't need shift bits.
	return   msr_info * rapl_unit.energy;
}
static inline float
read_PP0(int socket) {
	uint32_t msr_info = read_msr(msr_fds[socket], MSR_PP0_ENERGY_STATUS);
	return   msr_info * rapl_unit.energy;
}
static inline float
read_PP1(int socket) {
	uint32_t msr_info = read_msr(msr_fds[socket], MSR_PP1_ENERGY_STATUS);
	return   msr_info * rapl_unit.energy;
}

static inline float
read_DRAM(int socket) {
	uint32_t msr_info = read_msr(msr_fds[socket], MSR_DRAM_ENERGY_STATUS);
	return msr_info * dram_energy_unit;
}

void
ProfileInit() {
	char msr_filename[BUFSIZ];

	num_sockets = getSocketNum();
	micro_architecture_id = get_micro_architecture();
	power_domains_supported = get_power_domains_supported();
	uint64_t num_pkg_thread = get_num_pkg_thread();

	/*only two domains are supported for parameters check*/
	parameters = (rapl_msr_parameter *)malloc(2 * sizeof(rapl_msr_parameter));
	msr_fds = (int *) malloc(num_sockets * sizeof(int));

	int core = 0;
	for(int i = 0; i < num_sockets; i++) {
		if(i > 0) {
			core += num_pkg_thread / 2; 	//measure the first core of each package //@TODO: is the 2 because of HyperThreading??? I need to make sure of this because that's essential!!!!!!!!!!
		}
		sprintf(msr_filename, "/dev/cpu/%d/msr", core);
		msr_fds[i] = open(msr_filename, O_RDWR);
	}

	rapl_unit = get_rapl_unit(msr_fds[0]);

	energy_unit = rapl_unit.energy;
	rapl_wraparound = get_rapl_wraparound(energy_unit);

	dram_energy_unit = energy_unit;
	dram_rapl_wraparound = rapl_wraparound;

	bool case_for_special_dram_energy_unit = (
		is_this_the_current_architecture("BROADWELL") || is_this_the_current_architecture("BROADWELL2")
	);	
	if (case_for_special_dram_energy_unit) {
		dram_energy_unit = BROADWELL_MSR_DRAM_ENERGY_UNIT;
		dram_rapl_wraparound = get_rapl_wraparound(dram_energy_unit);
	}
}

void
ProfileDealloc() {
	for (int i = 0; i < num_sockets; i++)
		close(msr_fds[i]);
	free(msr_fds); msr_fds = NULL;
	free(parameters); parameters = NULL;
}

void
EnergyStatCheck(energy_stat_t energy_stat_per_socket[num_sockets]) {
	for (int socket = 0; socket < num_sockets; socket++) {
		energy_stat_per_socket[socket].timestamp = usec_since_epoch();
		energy_stat_per_socket[socket].dram = (power_domains_supported.dram) ? read_DRAM(socket) : -1;
		energy_stat_per_socket[socket].pp0  = (power_domains_supported.pp0 ) ? read_PP0 (socket) : -1;
		energy_stat_per_socket[socket].pp1  = (power_domains_supported.pp1 ) ? read_PP1 (socket) : -1;
		energy_stat_per_socket[socket].pkg  = (power_domains_supported.pkg ) ? read_PKG (socket) : -1;
	}
}

energy_measurement_t
measure_energy_between_stat_check(energy_stat_t start_stat, energy_stat_t stop_stat) {
	assert(rapl_wraparound != -1);

	energy_measurement_t diff;
	diff.dram = -1; diff.pp0 = -1; diff.pp1 = -1; diff.pkg  = -1;

	if (stop_stat.dram != -1 && start_stat.dram != -1) {
		diff.dram = stop_stat.dram - start_stat.dram;
		if (diff.dram < 0) {
			diff.dram += dram_rapl_wraparound;
		}
	} if (stop_stat.pp0 != -1 && start_stat.pp0 != -1) {
		diff.pp0 = stop_stat.pp0 - start_stat.pp0;
		if (diff.pp0 < 0) {
			diff.pp0 += rapl_wraparound;
		}
	} if (stop_stat.pp1 != -1 && start_stat.pp1 != -1) {
		diff.pp1  = stop_stat.pp1 - start_stat.pp1;
		if (diff.pp1 < 0) {
			diff.pp1 += rapl_wraparound;
		}
	} if (stop_stat.pkg != -1 && start_stat.pkg != -1) {
		diff.pkg  = stop_stat.pkg - start_stat.pkg;
		if (diff.pkg < 0) {
			diff.pkg += rapl_wraparound;
		}
	}

	diff.start_timestamp = start_stat.timestamp;
	diff.time_elapsed = stop_stat.timestamp - start_stat.timestamp;

	return diff;
}
