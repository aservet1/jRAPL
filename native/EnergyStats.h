#ifndef ENERGY_STATS_H
#define ENERGY_STATS_H

#include <sys/time.h>

typedef struct {
	int socket;
	double pkg;
	double dram;
	double gpu; //pp1
	double core;//pp0
	struct timeval timestamp;
} EnergyStats;

EnergyStats energy_stats_subtract(EnergyStats a, EnergyStats b);
int energy_stats_to_string(EnergyStats estats, char* ener_string, int power_domain);
int energy_stats_csv_string(EnergyStats estats, char* ener_string, int power_domain);

#endif //ENERGY_STATS_H
