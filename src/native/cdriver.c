#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "energy_check_utils.h"
#include "arch_spec.h"
#include "platform_support.h"

void sleep_print(int seconds) {
	for (int s = 1; s <= seconds; s++) {
		printf("%d\n",s);
		sleep(1);
	}
}

int main(int argc, const char* argv[]) {
	ProfileInit();
	power_domain_support_info_t pds = get_power_domains_supported();
	fprintf(
		stdout,
		"platform power domain support: {cpuid 0x%x, dram = %d, pp0 = %d, pp1 = %d, pkg = %d}\n",
		pds.cpuid, pds.dram, pds.pp0, pds.pp1, pds.pkg
	);
	uint32_t microarch = get_micro_architecture();

	fprintf(
		stdout,
		"reported microarch id: 0x%x\n",
		microarch
	);

	char arch_name_buf[32];
	get_arch_name(arch_name_buf);
	fprintf(
		stdout,
		"reported microarch name: %s\n",
		arch_name_buf
	);

	int num_sockets = getSocketNum();

	energy_stat_t energy_before[num_sockets];
	EnergyStatCheck(energy_before);
	for(int i = 0; i < num_sockets; ++i) {
		fprintf(
			stdout,
			"\nbefore: socket %d: {dram = %.6f, pkg = %.6f, pp0 = %.6f, pp1 = %.6f}\n",
			i, energy_before[i].dram, energy_before[i].pkg, energy_before[i].pp0, energy_before[i].pp1
		);
	}

	sleep(1);

	energy_stat_t energy_after[num_sockets];
	EnergyStatCheck(energy_after);
	for(int i = 0; i < num_sockets; ++i) {
		fprintf(
			stdout,
			"after:  socket %d: {dram = %.6f, pkg = %.6f, pp0 = %.6f, pp1 = %.6f}\n",
			i, energy_after[i].dram, energy_after[i].pkg, energy_after[i].pp0, energy_after[i].pp1
		);
	}

	fprintf(stdout,"\n");

	for (int i = 0; i < num_sockets; ++i) {
		energy_measurement_t joules = measure_energy_between_stat_check(energy_before[i],energy_after[i]);
		fprintf(
			stdout,
			"joules:  socket %d: {dram = %.6f, pkg = %.6f, pp0 = %.6f, pp1 = %.6f}\n",
			i, joules.dram, joules.pkg, joules.pp0, joules.pp1
		);
	}

	ProfileDealloc();
}
