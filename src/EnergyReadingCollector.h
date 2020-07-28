#ifndef _ENERGY_READING_COLLECTOR_H
#define _ENERGY_READING_COLLECTOR_H

#include <pthread.h>
#include <stdbool.h>

typedef struct Reading { //make each entry an array
	double dram_or_gpu; // or gpu
	double core;
	double package;
	// store timestamp, socket number, and reading
} Reading;

typedef struct ReadingList {
	Reading* items;
	unsigned long long capacity;
	unsigned long long nItems;
} ReadingList;

typedef struct {
	pthread_t* thread;
	int delay;
	ReadingList* readings;
	bool exit;
} ReadingCollector;


ReadingCollector* newReadingCollector(int delay, pthread_t* thread);
void start_collecting(ReadingCollector *collector);
void stop_collecting(ReadingCollector *collector);
void freeReadingCollector(ReadingCollector* collector);
void fileDump(ReadingCollector *collector, const char* filepath);

#endif
