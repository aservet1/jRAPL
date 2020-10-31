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
	if (storageType == DYNAMIC_ARRAY_STORAGE) {
		monitor->samples_dynarr = newDynamicArray(16);
		monitor->samples_linklist = NULL;
	} if (storageType == LINKED_LIST_STORAGE) {
		monitor->samples_dynarr = NULL;
		monitor->samples_linklist = newLinkedList();
	}
	monitor->storageType = storageType;
	return monitor;
}

void freeAsyncEnergyMonitor(AsyncEnergyMonitor* monitor)
{
	if (monitor->storageType == DYNAMIC_ARRAY_STORAGE) //@TODO make macros for checking the storage, more a e s t h e t i c
		freeDynamicArray(monitor->samples_dynarr);
	else if (monitor->storageType == LINKED_LIST_STORAGE)
		freeLinkedList(monitor->samples_linklist);
	free(monitor);
	monitor = NULL;
}

static void storeEnergySample(AsyncEnergyMonitor *monitor, EnergyStats stats)
{
	if (monitor->storageType == DYNAMIC_ARRAY_STORAGE)
		addItem_DynamicArray(monitor->samples_dynarr, stats);
	else if (monitor->storageType == LINKED_LIST_STORAGE)
		addItem_LinkedList(monitor->samples_linklist, stats);

	/*DynamicArray *samples_dynarr = monitor->samples_dynarr;
	if (samples_dynarr->nItems >= samples_dynarr->capacity)
	{
		samples_dynarr->capacity *= 2;
		samples_dynarr->items = realloc(samples_dynarr->items, samples_dynarr->capacity*sizeof(EnergyStats));
		assert(samples_dynarr->items != NULL);
	}
	samples_dynarr->items[samples_dynarr->nItems++] = stats;*/
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
			//printf("!!!%f,%f\n",stats[i].dram,stats[i].gpu);
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
	if (monitor->storageType == DYNAMIC_ARRAY_STORAGE) {
		freeDynamicArray(monitor->samples_dynarr);
		monitor->samples_dynarr = newDynamicArray(16);
	}
	else if (monitor->storageType == LINKED_LIST_STORAGE) {
		freeLinkedList(monitor->samples_linklist);
		monitor->samples_linklist = newLinkedList();
	}
}

void writeToFile(AsyncEnergyMonitor *monitor, const char* filepath){
	FILE * outfile = (filepath) ? fopen(filepath,"w") : stdout;

	fprintf(outfile,"samplingRate: %d milliseconds\n",monitor->samplingRate);
	fprintf(outfile,"socket,dram,gpu,cpu,pkg,timestamp,seconds/microseconds\n");
	
	if (monitor->storageType == DYNAMIC_ARRAY_STORAGE) {	
		/*EnergyStats* items = monitor->samples_dynarr->items;
		int nItems = monitor->samples_dynarr->nItems;
		for (int i = 0; i < nItems; i++) {
			EnergyStats current = items[i];
			char csv_string[512];
			energy_stats_csv_string(current, csv_string);
			fprintf(outfile,"%s\n",csv_string);
		}*/
		writeToFile_DynamicArray(outfile, monitor->samples_dynarr);
	} else if (monitor->storageType == LINKED_LIST_STORAGE) {
		writeToFile_LinkedList(outfile, monitor->samples_linklist);
	}

	if (filepath) fclose(outfile);
}

void lastKSamples(int k, AsyncEnergyMonitor* monitor, EnergyStats return_array[]) {
	if (monitor->storageType == DYNAMIC_ARRAY_STORAGE) {
		int sample_i = monitor->samples_dynarr->nItems-1; //start from the last one
		int return_i = k-1;
		do {
			return_array[return_i] = monitor->samples_dynarr->items[sample_i];
		} while ( --return_i >= 0 && --sample_i > 0);
	}
	else if (monitor->storageType == LINKED_LIST_STORAGE) {
		fprintf(stderr,"YOU HAVEN'T IMPLEMETED LINKED LIST STORAGE IN LASTKSAMPLES\n");
		exit(12);
	}
}



