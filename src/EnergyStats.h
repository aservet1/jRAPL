#ifndef ENERGY_STATS_H
#define ENERGY_STATS_H

#include <sys/time.h>

typedef struct {
	int socket;
	double pkg;
	double dram;
	double gpu;
	double cpu;
	struct timeval timestamp;
} EnergyStats;

EnergyStats energyStatsSubtract(EnergyStats a, EnergyStats b);














#endif //ENERGY_STATS_H
