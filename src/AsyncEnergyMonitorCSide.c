#include <stdio.h>
#include <time.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdbool.h>
#include <assert.h>
#include <errno.h>
#include <jni.h>
#include <sys/time.h>
#include "EnergyStats.h"
#include "AsyncEnergyMonitorCSide.h"
#include "CPUScaler.h"
#include "arch_spec.h"


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

static EnergySampleList* newEnergySampleList(unsigned long long capacity)
{
	EnergySampleList* list = (EnergySampleList*)malloc(sizeof(EnergySampleList));
	list->capacity = capacity;
	list->nItems = 0;
	list->items = (EnergyStats*)malloc(sizeof(EnergyStats)*capacity);
	return list;
}


AsyncEnergyMonitor* newAsyncEnergyMonitor(int samplingRate)
{
	AsyncEnergyMonitor* collector = (AsyncEnergyMonitor*)malloc(sizeof(AsyncEnergyMonitor));
	
	pthread_t thread;
	collector->thread = thread;
	collector->exit = false;
	collector->samplingRate = samplingRate;
	collector->samples = newEnergySampleList(16); 
	return collector;
}

static void freeEnergySampleList(EnergySampleList* list)
{
	free(list->items);
	free(list);
}

void freeAsyncEnergyMonitor(AsyncEnergyMonitor* collector)
{
	freeEnergySampleList(collector->samples);
	free(collector);
}

static void storeEnergySample(AsyncEnergyMonitor *collector, EnergyStats stats)
{
	EnergySampleList *samples = collector->samples;
	if (samples->nItems >= samples->capacity)
	{
		samples->capacity *= 2;
		samples->items = realloc(samples->items, samples->capacity*sizeof(EnergyStats));
		assert(samples->items != NULL);
	}
	samples->items[samples->nItems++] = stats;
}

void* run(void* collector_param){
	AsyncEnergyMonitor* collector = (AsyncEnergyMonitor*)collector_param;

	int sockets = getSocketNum();
	EnergyStats stats[sockets];

	while (!collector->exit)
	{
		EnergyStatCheck(stats); 

		for (int i = 0; i < sockets; i++) {
			storeEnergySample(collector,stats[i]);
		}
		
		sleep_millisecond(collector->samplingRate);
	}
	return NULL;
}


void start(AsyncEnergyMonitor *collector){
	pthread_create(&(collector->thread), NULL, run, collector);
}

void stop(AsyncEnergyMonitor *collector){
	collector->exit = true;
	pthread_join(collector->thread,NULL);
}

void reset(AsyncEnergyMonitor* collector){
	collector->exit = false;
	collector->samples->nItems = 0;
}

void writeToFile(AsyncEnergyMonitor *collector, const char* filepath){
	FILE * outfile = (filepath) ? fopen(filepath,"w") : stdout;
	
	EnergyStats* items = collector->samples->items;
	int nItems = collector->samples->nItems;
	EnergyStats current;
	fprintf(outfile,"samplingRate: %d milliseconds\n",collector->samplingRate);
	fprintf(outfile,"socket,dram,gpu,cpu,pkg,timestamp,seconds/microseconds\n");
	for (int i = 0; i < nItems; i++) {
		current = items[i];
		fprintf(outfile,"%d,%f,%f,%f,%f,%ld/%ld\n", current.socket, current.dram, 
				current.gpu, current.cpu, current.pkg, 
				current.timestamp.tv_sec, current.timestamp.tv_usec);
	}
	if (filepath) fclose(outfile);
}

void lastKSamples(int k, AsyncEnergyMonitor* collector, EnergyStats return_array[]) {
	int sample_i = collector->samples->nItems-1; //start from the last one
	int return_i = k-1;
	do { // :)
		return_array[return_i] = collector->samples->items[sample_i];
	} while ( --return_i >= 0 && --sample_i > 0);
}

/////////////////////////////////// JNI Calls Down Here ////////////////////////////////////////////////

/*

static AsyncEnergyMonitor* jniCollector = NULL; //managed by JNI function calls

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_initCollector(JNIEnv* env, jclass jcls, jint samplingRate)
{
	jniCollector = newAsyncEnergyMonitor((int)samplingRate);
}

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_freeCollector(JNIEnv* env, jclass jcls)
{
	freeAsyncEnergyMonitor(jniCollector);
	jniCollector = NULL;
}

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_startCollecting(JNIEnv* env, jclass jcls)
{
	printf("hello w0rld -- startCollecting\n");
	start(jniCollector);
}

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_stopCollecting(JNIEnv* env, jclass jcls)
{
	stop(jniCollector);
	printf("goodbye w0rld -- stopCollecting\n");
}

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_writeToFile(JNIEnv* env, jclass jcls, jstring filePath)
{
	writeToFile(jniCollector, (const char*)filePath);
}

*/
