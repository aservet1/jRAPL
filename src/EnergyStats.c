#include <stdio.h>
#include <assert.h>
#include <sys/time.h>
#include "EnergyStats.h"

EnergyStats energyStatsSubtract(EnergyStats x, EnergyStats y){ //@TODO -- implement the wraparound for negative values
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
