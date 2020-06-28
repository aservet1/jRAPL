#ifndef _ENERGY_READING_COLLECTOR_H
#define _ENERGY_READING_COLLECTOR_H

#include <pthread.h>


typedef struct{
    reading *nextReading;
    float dram; // or gpu
    float core;
    float package;
} reading;

typedef struct {
    pthread_t *thread;
    int delay;
    reading *readings;
    char keepReading;
} readingCollector;

void start_collecting(readingCollector *collector);
void stop_collection(readingCollector *collector);

#endif