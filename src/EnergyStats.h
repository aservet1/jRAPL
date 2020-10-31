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

EnergyStats energy_stats_subtract(EnergyStats a, EnergyStats b);
void energy_stats_to_string(EnergyStats estats, char ener_string[512]);
void energy_stats_csv_string(EnergyStats estats, char ener_string[512]);

#endif //ENERGY_STATS_H
