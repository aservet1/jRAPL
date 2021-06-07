#ifndef ENERGY_STATS_H
#define ENERGY_STATS_H

#include <sys/time.h>

typedef struct {
	double pkg;
	double dram;
	double gpu; //pp1
	double core;//pp0
	struct timeval timestamp;
} EnergyStats;

EnergyStats energy_stats_subtract(EnergyStats a, EnergyStats b);
void get_energy_stats_jni_string_format(char format_buffer[512]);
int energy_stats_to_string(EnergyStats estats, char* ener_string);
void energy_stats_csv_header(char* csv_header);
int energy_stats_csv_string(EnergyStats estats, int socket, char* csv_string);
int energy_stats_group_csv_string(EnergyStats estats, int socket, char* csv_string);
int energy_stats_to_jni_string(EnergyStats estats, char* ener_string);

#endif //ENERGY_STATS_H