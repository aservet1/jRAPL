#ifndef _ASYNC_ENERGY_MONITOR_CSIDE_H
#define _ASYNC_ENERGY_MONITOR_CSIDE_H

#include <pthread.h>
#include <stdbool.h>

#include "energy_check_utils.h"
#include "cside_data_storage.h"

#define DYNAMIC_ARRAY_STORAGE 1
#define LINKED_LIST_STORAGE 2

typedef struct AsyncEnergyMonitor {
	pthread_t tid;
	int samplingRate;
	//int nSamples;
	bool exit;
	//one of dynArr or linkList will be null
	//which one in use will be inidcated by storageType
	DynamicArray* samples_dynarr;
	LinkedList* samples_linklist;
	int storageType;

} AsyncEnergyMonitor;

AsyncEnergyMonitor* newAsyncEnergyMonitor(int delay, int storageType, size_t initialStorageSize);
void start(AsyncEnergyMonitor *monitor);
void stop(AsyncEnergyMonitor *monitor);
void freeAsyncEnergyMonitor(AsyncEnergyMonitor* monitor);
void writeFileCSV(AsyncEnergyMonitor *monitor, const char* filepath);
void lastKSamples(int k, AsyncEnergyMonitor* monitor, energy_measurement_t return_array[k]);
void reset(AsyncEnergyMonitor* monitor);
int getNumSamples(AsyncEnergyMonitor* monitor);
void setSamplingRate(AsyncEnergyMonitor* monitor, int s);
int getSamplingRate(AsyncEnergyMonitor* monitor);

#endif //_ASYNC_ENERGY_MONITOR_CSIDE_H
