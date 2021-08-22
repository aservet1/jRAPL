
#ifndef ENERGY_CHECK_UTILS_H
#define ENERGY_CHECK_UTILS_H

#include <sys/time.h>

typedef struct {
	float dram;
	float gpu; //pp1
	float core;//pp0
	float pkg;
	unsigned long timestamp;
} EnergyStats;

void ProfileInit();
void EnergyStatCheck(EnergyStats stats_per_socket[]);
void ProfileDealloc();
int* get_msr_fds();

EnergyStats energy_stats_subtract(EnergyStats a, EnergyStats b);
void energy_stats_csv_header(char* csv_header);
void energy_stats_csv_string(EnergyStats estats[], char* csv_string);

#endif //ENERGY_CHECK_UTILS_H
