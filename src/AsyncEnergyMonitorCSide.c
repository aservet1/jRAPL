#include <stdio.h>
#include <time.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdbool.h>
#include <assert.h>
#include <errno.h>
#include <jni.h>
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

static ReadingList* newReadingList(unsigned long long capacity)
{
	ReadingList* list = (ReadingList*)malloc(sizeof(ReadingList));
	list->capacity = capacity;
	list->nItems = 0;
	list->items = (Reading*)malloc(sizeof(Reading)*capacity);
	return list;
}

ReadingCollector* newReadingCollector(int delay, pthread_t thread)
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
		readings->items = realloc(readings->items, readings->capacity*sizeof(Reading));
		assert(readings->items != NULL);
		//printf("new capacity: %lld\n",readings->capacity);
	}
	readings->items[readings->nItems++] = r;
}

static Reading subtract_readings(Reading before, Reading after)
{
	Reading result;
	result.dram_or_gpu = after.dram_or_gpu - before.dram_or_gpu;
	result.core = after.core - before.core;
	result.package = after.package - before.package;
	return result;
}

static Reading parseReading(char* ener_info)
{
	Reading current;
	float dram_or_gpu, core, package;
	sscanf(ener_info, "%f#%f#%f", &dram_or_gpu, &core, &package);
	current.dram_or_gpu = dram_or_gpu;
	current.core = core;
	current.package = package;
	return current;
}

/*
// for debugging
static void printReadingList(ReadingList* readings)
{
	printf("%lld || ",readings->nItems);
	for (int i = 0; i < readings->nItems; i++)
	{
		Reading current = readings->items[i];
		printf("%f %f %f // ",current.dram_or_gpu,current.core,current.package);
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
*/

void* run(void* collector_param){
	ReadingCollector* collector = (ReadingCollector*)collector_param;
	char before_buffer[512];
	char after_buffer[512];
	while (!collector->exit) {
		EnergyStatCheck(before_buffer);		
		sleep_millisecond(collector->delay);
		EnergyStatCheck(after_buffer);

		Reading before = parseReading(before_buffer);
		Reading after = parseReading(after_buffer);
		
		Reading diff = subtract_readings(before, after);
		addReading(collector, diff);
	}
	return NULL;
}

void start_collecting(ReadingCollector *collector){
	pthread_create(&(collector->thread), NULL, run, collector);
	//printf("started\n");
}

void stop_collecting(ReadingCollector *collector){
	collector->exit = true;
	pthread_join(collector->thread,NULL);
}

void reset(ReadingCollector* collector){
	collector->exit = false;
	collector->readings->nItems = 0;
}

void fileDump(ReadingCollector *collector, const char* filepath){
	int dram_or_gpu = get_architecture_category(get_cpu_model());
	const char* dram_or_gpu_str = (dram_or_gpu == 1 || dram_or_gpu == 2) ? (dram_or_gpu == 1 ? "dram" : "gpu") : "undefined";
	FILE * outfile = (filepath) ? fopen(filepath,"w") : stdout;
	Reading* items = collector->readings->items;
	int nItems = collector->readings->nItems;
	Reading currentReading;
	fprintf(outfile,"delay: %d milliseconds\n",collector->delay);
	fprintf(outfile,"%s,core,pkg\n", dram_or_gpu_str);
	for (int i = 0; i < nItems; i++) {
		currentReading = items[i];
		fprintf(outfile,"%f,%f,%f\n", currentReading.dram_or_gpu, currentReading.core, currentReading.package);
	}
	printf("\n -- why does it have 0.000000 some times??? --\n\n");
	fclose(outfile);
}

/////////// JNI Calls Down Here /////////////
/*
static ReadingCollector* jniCollector; //managed by JNI function calls
static pthread_t* thread;

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_freeCollector(JNIEnv* env, jclass jcls)
{
	freeReadingCollector(jniCollector);
}

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_startCollecting(JNIEnv* env, jclass jcls, jint delay)
{
	printf("hello w0rld\n");
	jniCollector = newReadingCollector(delay, thread);
	start_collecting(jniCollector);
}

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_stopCollecting(JNIEnv* env, jclass jcls)
{
	stop_collecting(jniCollector);
	printf("goodbye w0rld\n");
}

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_writeToFile(JNIEnv* env, jclass jcls, jstring filePath)
{
	fileDump(jniCollector, (const char*)filePath);
}
*/
