#include <stdio.h>
#include <time.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdbool.h>
#include <assert.h>
#include "EnergyReadingCollector.h"
#include "CPUScaler.h"

/*int msleep(long msec){
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
}*/

static void msleep(int sec)
{
	sleep(sec); // just seconds for now, must make it millisecs later
}

static ReadingList* newReadingList(int capacity)
{
	ReadingList* list = (ReadingList*)malloc(sizeof(ReadingList));
	list->capacity = capacity;
	list->nItems = 0;
	list->items = (Reading*)malloc(sizeof(Reading)*capacity);
	return list;
}

ReadingCollector* newReadingCollector(int delay, pthread_t* thread)
{
	ReadingCollector* collector = (ReadingCollector*)malloc(sizeof(ReadingCollector));
	collector->thread = thread;
	collector->exit = false;
	collector->delay = delay;
	collector->readings = newReadingList(16); 
	return collector;
}

static void freeReadingList(ReadingList* list)
{
	free(list->items);
	free(list);
}

void freeReadingCollector(ReadingCollector* collector)
{
	freeReadingList(collector->readings);
	free(collector);
}

static void addReading(ReadingCollector *collector, Reading r)
{
	ReadingList *readings = collector->readings;
	if (readings->nItems >= readings->capacity)
	{
		readings->capacity *= 2;
		void* rc = realloc(readings->items, readings->capacity);
		assert(rc != NULL);
	}
	readings->items[readings->nItems++] = r;
}

static Reading subtract_readings(Reading before, Reading after)
{
	Reading result;
	result.dram = after.dram - before.dram;
	result.core = after.core - before.core;
	result.package = after.package - before.package;
	return result;
}

static Reading parseReading(char* ener_info)
{
	Reading current;
	float dram, core, package;
	sscanf(ener_info, "%f#%f#%f", &dram, &core, &package);
	current.dram = dram;
	current.core = core;
	current.package = package;
	return current;
}

// for debugging
static void printReadingList(ReadingList* readings)
{
	printf("%d || ",readings->nItems);
	for (int i = 0; i < readings->nItems; i++)
	{
		Reading current = readings->items[i];
		printf("%f %f %f // ",current.dram,current.core,current.package);
	}
	printf("\n");
}

// for debugging
static void printCollector(ReadingCollector* collector)
{
	printf("threadptr: %p\n",collector->thread);
	printf("delay: %d\n",collector->delay);
	printReadingList(collector->readings);
}



void* run(void* collector_param){
	ReadingCollector* collector = (ReadingCollector*)collector_param;
	char ener_info[512];
	while (!collector->exit) {
		EnergyStatCheck(ener_info);
		Reading before = parseReading(ener_info);
		
		msleep(collector->delay);
		
		EnergyStatCheck(ener_info);
		Reading after = parseReading(ener_info);
		
		Reading diff = subtract_readings(before, after);
		addReading(collector, diff);
	}
	return NULL;
}

void start_collecting(ReadingCollector *collector){
	pthread_create(collector->thread, NULL, run, collector);
	printf("started\n");
}

void stop_collecting(ReadingCollector *collector){
	collector->exit = true;
	pthread_join(*collector->thread,NULL);
}

void reset(ReadingCollector* collector){
	collector->exit = false;
	collector->readings->nItems = 0;
}

void fileDump(ReadingCollector *collector, const char* filepath){
	FILE * outfile= fopen(filepath,"w");
	Reading* items = collector->readings->items;
	int nItems = collector->readings->nItems;
	Reading currentReading;
	for (int i = 0; i < nItems; i++) {
		currentReading = items[i];
		fprintf(outfile,"one: %f\ttwo: %f\tthree: %f\n", currentReading.dram, currentReading.core, currentReading.package);
	}
	printf("\n -- why does it have 0.000000 some times??? --\n\n");
	fclose(outfile);
}






