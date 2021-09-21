
#ifndef ENERGY_CHECK_UTILS_H
#define ENERGY_CHECK_UTILS_H

#include <sys/time.h>

typedef struct {
	float dram;
	float gpu; //pp1
	float core;//pp0
	float pkg;
	unsigned long time;
} energy_info_t;

void ProfileInit();
void EnergyStatCheck(energy_info_t stats_per_socket[]);
void ProfileDealloc();
int* get_msr_fds();

void energy_stat_csv_header(char* csv_header);
void energy_diff_csv_header(char* csv_header);
void energy_stat_csv_string(energy_info_t estats[], char* csv_string);
energy_info_t subtract_energy_stat(energy_info_t a, energy_info_t b);

#endif //ENERGY_CHECK_UTILS_H
