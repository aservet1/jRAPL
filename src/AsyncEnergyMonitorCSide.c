#include <stdio.h>
#include <time.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdbool.h>
#include <assert.h>
#include <errno.h>
#include <jni.h>

#include "EnergyStats.h"
#include "AsyncEnergyMonitorCSide.h"
#include "CPUScaler.h"
#include "arch_spec.h"

#include "CSideDataStorage.h"

#define USING_DYNAMIC_ARRAY	monitor->storageType == DYNAMIC_ARRAY_STORAGE
#define USING_LINKED_LIST	monitor->storageType == LINKED_LIST_STORAGE

int sleep_millisecond(long msec){
	struct timespec ts;
	int res;

	if (msec < 0)
	{
		errno = EINVAL;
		return -1;
	}

	ts.tv_sec = msec / 1000;
	ts.tv_nsec = (msec % 1000) * 1000000;

	do {
		res = nanosleep(&ts, &ts);
	} while (res && errno == EINTR);

	return res;
}

AsyncEnergyMonitor* newAsyncEnergyMonitor(int samplingRate, int storageType)
{
	AsyncEnergyMonitor* monitor = (AsyncEnergyMonitor*)malloc(sizeof(AsyncEnergyMonitor));

	pthread_t thread;
	monitor->thread = thread;
	monitor->exit = false;
	monitor->samplingRate = samplingRate;
	monitor->storageType = storageType;
	if (USING_DYNAMIC_ARRAY) {
		monitor->samples_dynarr = newDynamicArray(16);
		monitor->samples_linklist = NULL;
	}
	if (USING_LINKED_LIST) {
		monitor->samples_dynarr = NULL;
		monitor->samples_linklist = newLinkedList();
	}
	return monitor;
}

void freeAsyncEnergyMonitor(AsyncEnergyMonitor* monitor)
{
	if (USING_DYNAMIC_ARRAY) {
		freeDynamicArray(monitor->samples_dynarr);
		monitor->samples_dynarr = NULL;
	}
	if (USING_LINKED_LIST) {
		freeLinkedList(monitor->samples_linklist);
		monitor->samples_linklist = NULL;
	}
	free(monitor);
	monitor = NULL;
}

static void storeEnergySample(AsyncEnergyMonitor *monitor, EnergyStats stats)
{
	if (monitor->storageType == DYNAMIC_ARRAY_STORAGE)
		addItem_DynamicArray(monitor->samples_dynarr, stats);
	if (monitor->storageType == LINKED_LIST_STORAGE)
		addItem_LinkedList(monitor->samples_linklist, stats);
}

void* run(void* monitor_arg)
{
	AsyncEnergyMonitor* monitor = (AsyncEnergyMonitor*)monitor_arg;

	int sockets = getSocketNum();
	EnergyStats stats[sockets];

	while (!monitor->exit)
	{
		EnergyStatCheck(stats); 
		for (int i = 0; i < sockets; i++) {
			storeEnergySample(monitor,stats[i]);
		}
		
		sleep_millisecond(monitor->samplingRate);
	}
	return NULL;
}

void start(AsyncEnergyMonitor *monitor){
	pthread_create(&(monitor->thread), NULL, run, monitor);
}

void stop(AsyncEnergyMonitor *monitor){
	monitor->exit = true;
	pthread_join(monitor->thread,NULL);
}

void reset(AsyncEnergyMonitor* monitor){
	monitor->exit = false;
	if (USING_DYNAMIC_ARRAY) {
		freeDynamicArray(monitor->samples_dynarr);
		monitor->samples_dynarr = newDynamicArray(16);
	}
	else if (USING_LINKED_LIST) {
		freeLinkedList(monitor->samples_linklist);
		monitor->samples_linklist = newLinkedList();
	}
}

void writeToFile(AsyncEnergyMonitor *monitor, const char* filepath){
	FILE * outfile = (filepath) ? fopen(filepath,"w") : stdout;

	fprintf(outfile,"samplingRate: %d milliseconds\n",monitor->samplingRate);
	fprintf(outfile,"socket,dram,gpu,core,pkg,timestamp,seconds/microseconds\n");
	
	if (USING_DYNAMIC_ARRAY)
		writeToFile_DynamicArray(outfile, monitor->samples_dynarr);
	if (USING_LINKED_LIST)
		writeToFile_LinkedList(outfile, monitor->samples_linklist);

	if (filepath) fclose(outfile);
}

void lastKSamples(int k, AsyncEnergyMonitor* monitor, EnergyStats return_array[]) {
	if (USING_DYNAMIC_ARRAY) {
		int sample_i = monitor->samples_dynarr->nItems-1; //start from the last one
		int return_i = k-1;
		do {
			return_array[return_i] = monitor->samples_dynarr->items[sample_i];
		} while ( --return_i >= 0 && --sample_i > 0);
	}
	else if (USING_LINKED_LIST) {
		fprintf(stderr,"YOU HAVEN'T IMPLEMETED LINKED LIST STORAGE IN LASTKSAMPLES\n");
		exit(12);
	}
}



