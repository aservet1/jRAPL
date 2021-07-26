#ifndef ENERGY_STATS_H
#define ENERGY_STATS_H

#include <sys/time.h>

typedef struct {
	float dram;
	float gpu; //pp1
	float core;//pp0
	float pkg;
	unsigned long timestamp;
} EnergyStats;

EnergyStats energy_stats_subtract(EnergyStats a, EnergyStats b);
void get_energy_stats_jni_string_format(char format_buffer[512]); //@TODO deprecate this, please
void energy_stats_csv_header(char* csv_header);
void energy_stats_csv_string(EnergyStats estats[], char* csv_string);

#endif //ENERGY_STATS_H
