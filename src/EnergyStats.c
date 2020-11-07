#include <stdio.h>
#include <assert.h>
#include <sys/time.h>
#include "EnergyStats.h"

EnergyStats
energy_stats_subtract(EnergyStats x, EnergyStats y){ //@TODO -- implement the wraparound for negative values
	assert(x.socket == y.socket);
	EnergyStats diff;
	diff.socket = x.socket;
	diff.dram = (x.dram != -1 && y.dram != -1) ? x.dram - y.dram : -1;
	diff.gpu = (x.gpu != -1 && y.gpu != -1) ? x.gpu - y.gpu : -1;
	diff.cpu = x.cpu - y.cpu;
	diff.pkg = x.pkg - y.pkg;
	gettimeofday(&diff.timestamp,NULL);
	return diff;
}


void
energy_stats_to_string(EnergyStats estats, char ener_string[512])
{
	sprintf(ener_string, "%f,%f,%f,%f@", estats.dram, estats.gpu, estats.cpu, estats.pkg);
}

void
energy_stats_csv_string(EnergyStats estats, char ener_string[512])
{
	sprintf(ener_string, "%d,%f,%f,%f,%f,%ld/%ld",
		estats.socket,
		estats.dram, estats.gpu, estats.cpu, estats.pkg, 
		estats.timestamp.tv_sec, estats.timestamp.tv_usec);
}
