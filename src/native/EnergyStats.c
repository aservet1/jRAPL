#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <sys/time.h>
#include <strings.h>
#include "EnergyStats.h"
#include "ArchSpec.h"

EnergyStats
energy_stats_subtract(EnergyStats x, EnergyStats y) { //@TODO -- implement the wraparound for negative values
	assert(x.socket == y.socket);
	EnergyStats diff;
	diff.socket = x.socket;
	diff.dram = (x.dram != -1 && y.dram != -1) ? x.dram - y.dram : -1;
	diff.gpu = (x.gpu != -1 && y.gpu != -1) ? x.gpu - y.gpu : -1;
	diff.core = x.core - y.core;
	diff.pkg = x.pkg - y.pkg;
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

static int
index_of_char(char buffer[], char char_key) {
	int i; for (i = 0; buffer[i] != char_key; i++);
	return (buffer[i] == char_key) ? i : -1;
}

void
energy_stats_csv_header(char csv_header[512]) {
	char buffer[512];
	get_energy_stats_jni_string_format(buffer);
	int index = index_of_char(buffer,'@');
	assert(index != -1 && "something went wrong in energy_stats_csv_header");
	buffer[index] = '\0';
	sprintf(csv_header, "%s,%s,%s", "socket", buffer, "timestamp");
}

int
energy_stats_csv_string(EnergyStats estats, char* csv_string) {
	switch (get_power_domains_supported(get_micro_architecture())) {
		case DRAM_GPU_CORE_PKG:
			return sprintf(csv_string, "%d,%.4f,%.4f,%.4f,%.4f,%ld",
				estats.socket,
				estats.dram,
				estats.gpu,
				estats.core,
				estats.pkg,
				(estats.timestamp.tv_sec * 1000000) + estats.timestamp.tv_usec
			);
		case GPU_CORE_PKG:
			return sprintf(csv_string, "%d,%.4f,%.4f,%.4f,%ld",
				estats.socket,
				estats.gpu,
				estats.core,
				estats.pkg,
				(estats.timestamp.tv_sec * 1000000) + estats.timestamp.tv_usec
			);
		case DRAM_CORE_PKG:
			return sprintf(csv_string, "%d,%.4f,%.4f,%.4f,%ld",
				estats.socket,
				estats.dram,
				estats.core,
				estats.pkg,
				(estats.timestamp.tv_sec * 1000000) + estats.timestamp.tv_usec
			);
		default:
			return -1;
	}
}
