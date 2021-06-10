#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <sys/time.h>
#include <strings.h>
#include "EnergyStats.h"
#include "ArchSpec.h"

#define timestampToUsec(ts) (ts.tv_sec * 1000000 + ts.tv_usec)

EnergyStats
energy_stats_subtract(EnergyStats x, EnergyStats y) {
	EnergyStats diff;
	//@TODO -- implement the wraparound for negative values
	diff.dram = (x.dram != -1 && y.dram != -1) ? x.dram - y.dram : -1;
	diff.gpu  = (x.gpu  != -1 && y.gpu != -1) ? x.gpu - y.gpu : -1;
	diff.core = (x.core  - y.core);
	diff.pkg  = (x.pkg   - y.pkg);
	gettimeofday(&diff.timestamp,NULL); // right now, when you subtract two EnergyStats structs, the timestamp of the result is just a timestamp for its creation date
	return diff;
}

static void
multiply_string_by_socket_num(char buffer[], char string[]) {
	int socketnum = getSocketNum();
	int string_len = strlen(string);
	int offset = 0;
	for (int i = 0; i < socketnum; i++) {
		memcpy(buffer + offset, string, string_len);
		offset += string_len;
	}
	buffer[++offset] = '\0';
}

void
get_energy_stats_jni_string_format(char buffer[512]) {
	char* string;
	switch (get_power_domains_supported(get_micro_architecture())) {
		case DRAM_GPU_CORE_PKG:
			string = "dram,gpu,core,pkg@";
			break;
		case DRAM_CORE_PKG:
			string = "dram,core,pkg@";
			break;
		case GPU_CORE_PKG:
			string = "gpu,core,pkg@";
			break;
		default:
			sprintf(buffer,"undefined_architecture@");
			return;
	}
	bzero(buffer, 512);
	multiply_string_by_socket_num(buffer, string);
	return;
}

int
energy_stats_to_jni_string(EnergyStats estats, char* ener_string) {
	switch (get_power_domains_supported(get_micro_architecture())) {
		case DRAM_GPU_CORE_PKG:
			return sprintf(ener_string, "%.4f,%.4f,%.4f,%.4f@",
				estats.dram,
				estats.gpu,
				estats.core,
				estats.pkg
			);
		case GPU_CORE_PKG:
			return sprintf(ener_string, "%.4f,%.4f,%.4f@",
				estats.gpu,
				estats.core,
				estats.pkg
			);
		case DRAM_CORE_PKG:
			return sprintf(ener_string, "%.4f,%.4f,%.4f@",
				estats.dram,
				estats.core,
				estats.pkg
			);
		default:
			return -1;
	}
}

static void
replace_chars(char* buf, char search, char replace, int len) {
	for (int i = 0; i < strlen(buf); i++)
		if (buf[i] == search)
			buf[i] = replace;
}

void
energy_stats_csv_header(char csv_header[512]) {
	char buffer[512];
	get_energy_stats_jni_string_format(buffer);
	replace_chars(buffer, '@', ',');
	sprintf(csv_header, "%s,%s", buffer, "timestamp");
}

int
<<<<<<< HEAD
energy_stats_csv_string(EnergyStats estats, int socket, char* csv_string) {
	switch (get_power_domains_supported(get_cpu_model())) {
=======
energy_stats_csv_string(EnergyStats estats, char* csv_string) {
	switch (get_power_domains_supported(get_micro_architecture())) {
>>>>>>> master
		case DRAM_GPU_CORE_PKG:
			return sprintf(csv_string, "%.4f,%.4f,%.4f,%.4f,%ld",
				socket,
				estats.dram,
				estats.gpu,
				estats.core,
				estats.pkg,
				timestampToUsec(estats.timestamp)
			);
		case GPU_CORE_PKG:
			return sprintf(csv_string, "%d,%.4f,%.4f,%.4f,%ld",
				socket,
				estats.gpu,
				estats.core,
				estats.pkg,
				timestampToUsec(estats.timestamp)
			);
		case DRAM_CORE_PKG:
			return sprintf(csv_string, "%d,%.4f,%.4f,%.4f,%ld",
				socket,
				estats.dram,
				estats.core,
				estats.pkg,
				timestampToUsec(estats.timestamp)
			);
		default:
			return -1;
	}
}

void
energy_stats_group_csv_string(EnergyStats estats[], char* csv_string) {
	int offset = 0;
	int powdom get_power_domains_supported(get_cpu_model());
	int sockets = getSOcketNum();
	for (int i = 0; i < sockets; i++) {
		switch (powdom) {
			case DRAM_GPU_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.4f,%.4f,%.4f,%.4f",
					estats.dram,
					estats.gpu,
					estats.core,
					estats.pkg
				);
			case GPU_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.4f,%.4f,%.4f",
					estats.gpu,
					estats.core,
					estats.pkg
				);
			case DRAM_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.4f,%.4f,%.4f",
					estats.dram,
					estats.core,
					estats.pkg
				);
			default:
				assert(false && "error occurred in energy_stats_group_csv_string");
		}
	}
	sprintf(csv_string+offset, "%ld", timestampToUsec(estats[0].timestamp) );
}
