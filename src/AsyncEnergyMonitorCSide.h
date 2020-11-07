#ifndef _ASYNC_ENERGY_MONITOR_CSIDE_H
#define _ASYNC_ENERGY_MONITOR_CSIDE_H

#include <pthread.h>
#include <stdbool.h>

#include "EnergyStats.h"
#include "CSideDataStorage.h"

#define DYNAMIC_ARRAY_STORAGE 1
#define LINKED_LIST_STORAGE 2

typedef struct AsyncEnergyMonitor {
	pthread_t thread;
	int samplingRate;
	bool exit;

	//one of dynArr or linkList will be null
	//which one in use will be inidcated by storageType
	DynamicArray* samples_dynarr;
	LinkedList* samples_linklist;
	int storageType;

	//void (*addSample)(AsyncEnergyMonitor*);

} AsyncEnergyMonitor;


AsyncEnergyMonitor* newAsyncEnergyMonitor(int delay, int storageType);
void start(AsyncEnergyMonitor *collector);
void stop(AsyncEnergyMonitor *collector);
void freeAsyncEnergyMonitor(AsyncEnergyMonitor* collector);
void writeToFile(AsyncEnergyMonitor *collector, const char* filepath);
void lastKSamples(int k, AsyncEnergyMonitor* collector, EnergyStats return_array[k]);

#endif //_ASYNC_ENERGY_MONITOR_CSIDE_H
