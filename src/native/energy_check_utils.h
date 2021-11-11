#ifndef ENERGY_CHECK_UTILS_H
#define ENERGY_CHECK_UTILS_H

#include <sys/time.h>

typedef struct {
	float dram;
	float gpu; //pp1;
	float core;//pp0;
	float pkg;
	unsigned long timestamp;
} energy_stat_t;

typedef struct {
	float dram;
	float gpu; //pp1;
	float core;//pp0;
	float pkg;
	unsigned long start_timestamp;
	unsigned long time_elapsed;
} energy_measurement_t;

void ProfileInit();
void EnergyStatCheck(energy_stat_t energy_stat_persocket[]);
void ProfileDealloc();

int* get_msr_fds();

void set_csv_delimiter(char c);

void energy_stat_csv_header(char* csv_header);
void energy_stat_csv_string(energy_stat_t energy_stat_per_socket[], char* csv_string);

void energy_measurement_csv_header(char* csv_header);
void energy_measurement_csv_string(energy_measurement_t energy_measurement_per_socket[], char* csv_string);

energy_measurement_t measure_energy_between_stat_check(energy_stat_t a, energy_stat_t b);

#endif //ENERGY_CHECK_UTILS_H
