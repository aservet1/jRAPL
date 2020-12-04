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
		monitor->samples_dynarr = newDynamicArray(64);
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
	if (USING_DYNAMIC_ARRAY)
		addItem_DynamicArray(monitor->samples_dynarr, stats);
	if (USING_LINKED_LIST)
		addItem_LinkedList(monitor->samples_linklist, stats);
}

void* run(void* monitor_arg)
{
	AsyncEnergyMonitor* monitor = (AsyncEnergyMonitor*)monitor_arg;

	int sockets = getSocketNum();
	EnergyStats stats[sockets];

	while (!monitor->exit)
	{
		EnergyStatCheck(stats,ALL_SOCKETS); 
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
	fprintf(outfile,"socket,dram,gpu,core,pkg,timestamp(usec since epoch)\n");
	
	if (USING_DYNAMIC_ARRAY)
		writeToFile_DynamicArray(outfile, monitor->samples_dynarr);
	if (USING_LINKED_LIST)
		writeToFile_LinkedList(outfile, monitor->samples_linklist);

	if (filepath) fclose(outfile);
}


// for some reason return_array needs to be allocated on the heap, passing a
//  stack-allocated array copies everything into the array, but then alters
//  the pointer to something that segfaults when you try to access it. but this
//  fills and works just fine when it's heap-allocated
//
// Ok so sometimes it works fine when stack-allocated but still...leave this
//  note here until you find out what's going on / how to prevent the issue
//  from happening
void lastKSamples(int k, AsyncEnergyMonitor* monitor, EnergyStats* return_array) {

	int nItems = (
		(USING_DYNAMIC_ARRAY) ?
		monitor->samples_dynarr->nItems : monitor->samples_linklist->nItems
	);

	int start = nItems-k;

	if (start < 0) {
		start = 0;
		k = nItems;
	}
	
	int returnArrayIndex = 0;

	if (USING_DYNAMIC_ARRAY) {
		for (int i = start; i < monitor->samples_dynarr->nItems; i++) {
			return_array[returnArrayIndex++] = monitor->samples_dynarr->items[i];
		}
	}

	if (USING_LINKED_LIST) { // turns out extracting lastK from this type of data structure is a but tricky...
		LinkedList* list = monitor->samples_linklist;
		// find which node contains last k'th element (the same as the start'th element)
		LinkNode* current = list->head;
		int current_upperbound = NODE_CAPACITY;
		while (current_upperbound < start) {
			current = current->next;
			current_upperbound += NODE_CAPACITY;
		}
		// copy over the relevant parts of this node
		current_upperbound = (current == list->tail) ? list->nItemsAtTail : NODE_CAPACITY;

		for (int i = start % NODE_CAPACITY ; i < current_upperbound; i++) {
			return_array[returnArrayIndex++] = current->items[i];
		}
		current = current->next;
		while (current != NULL) {
			current_upperbound = (current != list->tail)
				? NODE_CAPACITY
				: list->nItemsAtTail;
			for ( int i = 0; i < current_upperbound; i++ ) {
				return_array[returnArrayIndex++] = current->items[i];
			}
			current = current->next;
		}
	}
	//printf("return_array at end of function: %p\n",return_array);

	//if (USING_DYNAMIC_ARRAY) {
	//	int start = monitor->samples_dynarr->nItems-k;
	//	int arrayIndex = 0;

	//	if (start < 0) {
	//		start = 0;
	//		k = monitor->samples_dynarr->nItems;
	//	}

	//	for (int i = start; i < monitor->samples_dynarr->nItems; i++)
	//		return_array[arrayIndex++] = monitor->samples_dynarr->items[i];

	//	return;

	//}
	//else if (USING_LINKED_LIST) {

	//	int upperbound = NODE_CAPACITY;
	//	LinkNode* current = monitor->samples_linklist->head;
	//	while ( upperbound < k ) {
	//		current = current->next;
	//		upperBound += NODE_CAPACITY;
	//	}	

	//}
}














