#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdbool.h>
#include <assert.h>

#include "async_energy_monitor.h"
#include "energy_check_utils.h"
#include "arch_spec.h"
#include "utils.h"

#include "cside_data_storage.h"

#define USING_DYNAMIC_ARRAY	monitor->storageType == DYNAMIC_ARRAY_STORAGE
#define USING_LINKED_LIST	monitor->storageType == LINKED_LIST_STORAGE

void
setSamplingRate(AsyncEnergyMonitor* monitor, int s) {
	monitor->samplingRate = s;
}

int
getSamplingRate(AsyncEnergyMonitor* monitor) {
	return monitor->samplingRate;
}

int
getNumSamples(AsyncEnergyMonitor* monitor) {
	int n;
	if (USING_DYNAMIC_ARRAY) n = monitor->samples_dynarr->nItems;
	else if (USING_LINKED_LIST) n = monitor->samples_linklist->nItems;
	else n = -1;
	return n / ((int)getSocketNum()); /*
		the data structure stores each individual reading,
		but we want to think of samples as a group of
		readings per socket
	*/
}

AsyncEnergyMonitor*
newAsyncEnergyMonitor(int samplingRate, int storageType, size_t initialStorageSize) { // size_parameter is array size for DynArr and node capacity for LinkedList
	AsyncEnergyMonitor* monitor = (AsyncEnergyMonitor*)malloc(sizeof(AsyncEnergyMonitor));
	monitor->exit = false;
	monitor->samplingRate = samplingRate;
	monitor->storageType = storageType;
	if (USING_DYNAMIC_ARRAY) {
		monitor->samples_dynarr = newDynamicArray(initialStorageSize*getSocketNum()); // while the lists store items as individual energy_measurement_t objs, they're conceptually grouped as energy_measurement_t readings for all sockets. so an 'item' in the underlying data structure is one energy_measurement_t struct. but an 'item' from the perspective of the user is a list of energy_measurement_t structs, each pertaining to the readings of a given socket. the structs are stored in order, so the first one is for socket1, the second one is for socket2, etc.
		monitor->samples_linklist = NULL;
	}
	if (USING_LINKED_LIST) {
		monitor->samples_dynarr = NULL;
		monitor->samples_linklist = newLinkedList(initialStorageSize*getSocketNum());
	}
	return monitor;
}

void
freeAsyncEnergyMonitor(AsyncEnergyMonitor* monitor) {
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

static void
storeEnergyMeasurement(AsyncEnergyMonitor *monitor, energy_measurement_t energy_measurement) {
	if (USING_DYNAMIC_ARRAY)
		addItem_DynamicArray(monitor->samples_dynarr, energy_measurement);
	else if (USING_LINKED_LIST)
		addItem_LinkedList(monitor->samples_linklist, energy_measurement);
}

void*
run(void* monitor_arg) {
	AsyncEnergyMonitor* monitor = (AsyncEnergyMonitor*)monitor_arg;

	int sockets = getSocketNum();
	energy_stat_t before[sockets];
	energy_stat_t  after[sockets];

	EnergyStatCheck(before);
	while (!monitor->exit) {
		sleep_millisecond(monitor->samplingRate);
		EnergyStatCheck(after);
		for (int i = 0; i < sockets; i++) {
			storeEnergyMeasurement (
				monitor,
				measure_energy_between_stat_check ( before[i], after[i] )
			);
			before[i] = after[i];
		}
		// for (int i = 0; i < sockets)
		// before = after;
	}

	return NULL;
}

void
start(AsyncEnergyMonitor *monitor) {
	pthread_create(&(monitor->tid), NULL, run, monitor);
}

void
stop(AsyncEnergyMonitor *monitor) {
	monitor->exit = true;
	pthread_join(monitor->tid,NULL);
}

void
reset(AsyncEnergyMonitor* monitor) {
	monitor->exit = false;
	if (USING_DYNAMIC_ARRAY) {
		freeDynamicArray(monitor->samples_dynarr);
		monitor->samples_dynarr = newDynamicArray(16);
	}
	else if (USING_LINKED_LIST) {
		size_t node_capacity = monitor->samples_linklist->node_capacity;
		freeLinkedList(monitor->samples_linklist);
		monitor->samples_linklist = newLinkedList(node_capacity);
	}
}

void
writeFileCSV(AsyncEnergyMonitor *monitor, const char* filepath) {
	FILE * outfile = (filepath) ? fopen(filepath,"w") : stdout;

	if (!outfile) {
		fprintf(stderr,"ERROR in writeFileCSV, could not create output file for filepath = %s\n",filepath);
		exit(1);
	}
	char csv_header[512];
	energy_measurement_csv_header(csv_header);
	fprintf(outfile,"%s\n",csv_header);

	if (USING_DYNAMIC_ARRAY) writeFileCSV_DynamicArray(outfile, monitor->samples_dynarr);
	if (USING_LINKED_LIST)   writeFileCSV_LinkedList(outfile, monitor->samples_linklist);

	if (filepath) fclose(outfile);
}


// For some reason return_array needs to be allocated on the heap, passing a
//  stack-allocated array copies everything into the array, but then alters
//  the pointer to something that segfaults when you try to access it. but this
//  fills and works just fine when it's heap-allocated
//
// Ok so sometimes it works fine when stack-allocated but still...leave this
//  note here until you find out what's going on / how to prevent the issue
//  from happening
void
lastKSamples(int k, AsyncEnergyMonitor* monitor, energy_measurement_t* return_buffer) {

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
			return_buffer[returnArrayIndex++] = monitor->samples_dynarr->items[i];
		}
	}

	if (USING_LINKED_LIST) { // turns out extracting lastK from this type of data structure is a but tricky...
		LinkedList* list = monitor->samples_linklist;
		// find which node contains last k'th element (the same as the start'th element)
		LinkNode* current = list->head;
		int current_upperbound = list->node_capacity;
		while (current_upperbound < start) {
			current = current->next;
			current_upperbound += list->node_capacity;
		}
		// copy over the relevant parts of this node
		current_upperbound = (current == list->tail) ? list->nItemsAtTail : list->node_capacity;

		for (int i = start % list->node_capacity ; i < current_upperbound; i++) {
			return_buffer[returnArrayIndex++] = current->items[i];
		}
		current = current->next;
		while (current != NULL) {
			current_upperbound = (current != list->tail)
				? list->node_capacity
				: list->nItemsAtTail;
			for ( int i = 0; i < current_upperbound; i++ ) {
				return_buffer[returnArrayIndex++] = current->items[i];
			}
			current = current->next;
		}
	}
}
