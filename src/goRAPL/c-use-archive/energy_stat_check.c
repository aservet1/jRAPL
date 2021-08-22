#include <stdio.h>

#include "arch_spec.h"
#include "energy_check_utils.h"

static EnergyStats
energyStatCheck(int socket) {
	EnergyStats stats[getSocketNum()];
	EnergyStatCheck(stats);
	return stats[socket-1];
}

int main() {
	ProfileInit();

	EnergyStats stat = energyStatCheck(1);
	printf (
		"{\"dram\": %f, \"gpu\": %f, \"core\": %f, \"pkg\": %f, \"timestamp\": %ld}\n",
		stat.dram, stat.gpu, stat.core, stat.pkg, stat.timestamp
	);

	ProfileDealloc();
}
