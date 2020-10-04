#ifndef _ASYNC_ENERGY_MONITOR_CSIDE_H
#define _ASYNC_ENERGY_MONITOR_CSIDE_H

#include <pthread.h>
#include <stdbool.h>

#include "EnergyStats.h"

typedef struct EnergySampleList {
	EnergyStats* items;
	unsigned long long capacity;
	unsigned long long nItems;
} EnergySampleList;

typedef struct AsyncEnergyMonitor {
	pthread_t thread;
	int samplingRate;
	EnergySampleList* samples;
	bool exit;
} AsyncEnergyMonitor;


AsyncEnergyMonitor* newAsyncEnergyMonitor(int delay);
void start(AsyncEnergyMonitor *collector);
void stop(AsyncEnergyMonitor *collector);
void freeAsyncEnergyMonitor(AsyncEnergyMonitor* collector);
void writeToFile(AsyncEnergyMonitor *collector, const char* filepath);


#endif //_ASYNC_ENERGY_MONITOR_CSIDE_H
