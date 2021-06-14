#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <strings.h>
#include "EnergyStats.h"
#include "ArchSpec.h"

EnergyStats
energy_stats_subtract(EnergyStats x, EnergyStats y) {
	EnergyStats diff; //@TODO -- implement the wraparound for negative values
	diff.dram = (x.dram != -1 && y.dram != -1) ? x.dram - y.dram : -1;
	diff.core = (x.core != -1 && y.core != -1) ? x.core - y.core : -1;
	diff.gpu  = (x.gpu  != -1 && y.gpu  != -1) ? x.gpu  - y.gpu  : -1;
	diff.pkg  = (x.pkg  != -1 && y.pkg  != -1) ? x.pkg  - y.pkg  : -1;
	diff.timestamp = x.timestamp - y.timestamp;	
	return diff;
}

// TODO a whole lot of the ways I come up with formats and labels and stuff is pretty janky. it all works for the purposes
// of what needs to happen, but it's poorly designed and probably hard for someone to edit without a lot of time to
// figure out what's going on in this code. make sure to edit before jRAPL is declared "release-able"

// string processing in C is annoying lol

// static void label(char buffer[]) {
// 	switch (get_power_domains_supported(get_micro_architecture())) {
// 		case DRAM_GPU_CORE_PKG:
// 			sprintf(buffer,"dram,gpu,core,pkg");
// 			break;
// 		case DRAM_CORE_PKG:
// 			sorintf(buffer,"dram,core,pkg");
// 			break;
// 		case GPU_CORE_PKG:
// 			sprintf(buffer,"gpu,core,pkg");
// 			break;
// 		default:
// 			sprintf(buffer,"undefined");
// 			return;
// 	}
// }

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

// static void
// replace_chars(char* buf, char search, char replace, int len) {
// 	for (int i = 0; i < strlen(buf); i++)
// 		if (buf[i] == search)
// 			buf[i] = replace;
// }

// TODO see if there's a way you can get around that, to make the code more modular instead of a copy/paste for each case
//	that you have to edit slightly
// because sprintf requires me to hard code the number of arguments, I have to violate the DRY principle for this function
void
energy_stats_csv_header(char csv_header[512]) { 
	int num_sockets = getSocketNum();
	int offset = 0;
	const char* format;
	switch(get_power_domains_supported(get_micro_architecture())) {
		case DRAM_GPU_CORE_PKG:
			format = "dram_socket%d,gpu_socket_%d,core_socket%d,pkg_socket%d";
			for (int s = 1; s <= num_sockets; s++) {
				offset += sprintf(csv_header + offset, format, s,s,s,s);
				if (s <= num_sockets-1) offset += sprintf(csv_header+offset,",");
			}
			return;
		case DRAM_CORE_PKG:
			format = "dram_socket%d,core_socket%d,pkg_socket%d";
			for (int s = 1; s <= num_sockets; s++) {
				offset += sprintf(csv_header + offset, format, s,s,s);
				if (s <= num_sockets-1) offset += sprintf(csv_header+offset,",");
			}
			return;
		case GPU_CORE_PKG:
			format = "gpu_socket_%d,core_socket%d,pkg_socket%d";
			for (int s = 1; s <= num_sockets; s++) {
				offset += sprintf(csv_header + offset, format, s,s,s);
				if (s <= num_sockets-1) offset += sprintf(csv_header+offset,",");
			}
			return;
		default:
			sprintf(csv_header, "undefined_architecture");
			return;
	}

	// char buffer[512];
	// get_energy_stats_jni_string_format(buffer);
	// replace_chars(buffer, '@', ',', strlen(buffer));
	// sprintf(csv_header, "%s%s", buffer, "timestamp");
}

// int
// energy_stats_csv_string(EnergyStats estats, int socket, char* csv_string) {
// 	switch (get_power_domains_supported(get_micro_architecture())) {
// 		case DRAM_GPU_CORE_PKG:
// 			return sprintf(csv_string, "%d,%.4f,%.4f,%.4f,%.4f,%ld",
// 				socket,
// 				estats.dram,
// 				estats.gpu,
// 				estats.core,
// 				estats.pkg,
// 				estats.timestamp
// 			);
// 		case GPU_CORE_PKG:
// 			return sprintf(csv_string, "%d,%.4f,%.4f,%.4f,%ld",
// 				socket,
// 				estats.gpu,
// 				estats.core,
// 				estats.pkg,
// 				estats.timestamp
// 			);
// 		case DRAM_CORE_PKG:
// 			return sprintf(csv_string, "%d,%.4f,%.4f,%.4f,%ld",
// 				socket,
// 				estats.dram,
// 				estats.core,
// 				estats.pkg,
// 				estats.timestamp
// 			);
// 		default:
// 			return -1;
// 	}
// }

void
energy_stats_csv_string(EnergyStats estats[], char* csv_string) {
// energy_stats_group_csv_string(EnergyStats estats[], char* csv_string) {
	int offset = 0;
	int power_domains = get_power_domains_supported(get_micro_architecture());

	int sockets = getSocketNum();
	for (int i = 0; i < sockets; i++) {
		switch (power_domains) {
			case DRAM_GPU_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.4f,%.4f,%.4f,%.4f",
					estats[i].dram,
					estats[i].gpu,
					estats[i].core,
					estats[i].pkg
				);
				break;
			case GPU_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.4f,%.4f,%.4f",
					estats[i].gpu,
					estats[i].core,
					estats[i].pkg
				);
				break;
			case DRAM_CORE_PKG:
				offset += sprintf(csv_string+offset, "%.4f,%.4f,%.4f",
					estats[i].dram,
					estats[i].core,
					estats[i].pkg
				);
				break;
			default:
				assert(0 && "error occurred in energy_stats_csv_string");
		}
	}
	sprintf(csv_string+offset, ",%ld", estats[0].timestamp);
}
