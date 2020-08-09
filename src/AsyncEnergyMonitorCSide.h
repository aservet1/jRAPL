#ifndef _ASYNC_ENERGY_MONITOR_C_SIDE_H
#define _ASYNC_ENERGY_MONITOR_C_SIDE_H

#include <pthread.h>
#include <stdbool.h>

typedef struct EnergySample { //make each entry an array
	double dram_or_gpu; // or gpu
	double core;
	double package;
	// store timestamp, socket number, and reading
} EnergySample;

typedef struct EnergySampleList {
	EnergySample* items;
	unsigned long long capacity;
	unsigned long long nItems;
} EnergySampleList;

typedef struct {
	pthread_t thread;
	int samplingRate;
	EnergySampleList* samples;
	bool exit;
} AsyncEnergyMonitor;


AsyncEnergyMonitor* newAsyncEnergyMonitor(int delay, pthread_t thread);
void start(AsyncEnergyMonitor *collector);
void stop(AsyncEnergyMonitor *collector);
void freeAsyncEnergyMonitor(AsyncEnergyMonitor* collector);
void writeToFile(AsyncEnergyMonitor *collector, const char* filepath);


#endif //_ASYNC_ENERGY_MONITOR_C_SIDE_H
