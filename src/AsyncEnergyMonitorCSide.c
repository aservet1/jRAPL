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

static EnergySampleList* newEnergySampleList(unsigned long long capacity)
{
	EnergySampleList* list = (EnergySampleList*)malloc(sizeof(EnergySampleList));
	list->capacity = capacity;
	list->nItems = 0;
	list->items = (EnergySample*)malloc(sizeof(EnergySample)*capacity);
	return list;
}

AsyncEnergyMonitor* newAsyncEnergyMonitor(int samplingRate, pthread_t thread)
{
	AsyncEnergyMonitor* collector = (AsyncEnergyMonitor*)malloc(sizeof(AsyncEnergyMonitor));
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

static void addEnergySample(AsyncEnergyMonitor *collector, EnergySample r)
{
	EnergySampleList *samples = collector->samples;
	if (samples->nItems >= samples->capacity)
	{
		samples->capacity *= 2;
		samples->items = realloc(samples->items, samples->capacity*sizeof(EnergySample));
		assert(samples->items != NULL);
		//printf("new capacity: %lld\n",samples->capacity);
	}
	samples->items[samples->nItems++] = r;
}

static EnergySample subtract_samples(EnergySample before, EnergySample after)
{
	EnergySample result;
	result.dram_or_gpu = after.dram_or_gpu - before.dram_or_gpu;
	result.core = after.core - before.core;
	result.package = after.package - before.package;
	return result;
}

static EnergySample parseEnergySample(char* ener_info)
{
	EnergySample current;
	float dram_or_gpu, core, package;
	sscanf(ener_info, "%f#%f#%f", &dram_or_gpu, &core, &package);
	current.dram_or_gpu = dram_or_gpu;
	current.core = core;
	current.package = package;
	return current;
}

/*
// for debugging
static void printEnergySampleList(EnergySampleList* samples)
{
	printf("%lld || ",samples->nItems);
	for (int i = 0; i < samples->nItems; i++)
	{
		EnergySample current = samples->items[i];
		printf("%f %f %f // ",current.dram_or_gpu,current.core,current.package);
	}
	printf("\n");
}

// for debugging
static void printCollector(AsyncEnergyMonitor* collector)
{
	printf("threadptr: %p\n",collector->thread);
	printf("samplingRate: %d\n",collector->samplingRate);
	printEnergySampleList(collector->samples);
}
*/

void* run(void* collector_param){
	AsyncEnergyMonitor* collector = (AsyncEnergyMonitor*)collector_param;
	char before_buffer[512];
	char after_buffer[512];
	while (!collector->exit) {
		EnergyStatCheck(before_buffer);		
		sleep_millisecond(collector->samplingRate);
		EnergyStatCheck(after_buffer);

		EnergySample before = parseEnergySample(before_buffer);
		EnergySample after = parseEnergySample(after_buffer);
		
		EnergySample diff = subtract_samples(before, after);
		addEnergySample(collector, diff);
	}
	return NULL;
}

void start(AsyncEnergyMonitor *collector){
	pthread_create(&(collector->thread), NULL, run, collector);
	//printf("started\n");
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
	int dram_or_gpu = get_architecture_category(get_cpu_model());
	const char* dram_or_gpu_str = (dram_or_gpu == 1 || dram_or_gpu == 2) ? (dram_or_gpu == 1 ? "dram" : "gpu") : "undefined";
	FILE * outfile = (filepath) ? fopen(filepath,"w") : stdout;
	EnergySample* items = collector->samples->items;
	int nItems = collector->samples->nItems;
	EnergySample currentEnergySample;
	fprintf(outfile,"samplingRate: %d milliseconds\n",collector->samplingRate);
	fprintf(outfile,"%s,core,pkg\n", dram_or_gpu_str);
	for (int i = 0; i < nItems; i++) {
		currentEnergySample = items[i];
		fprintf(outfile,"%f,%f,%f\n", currentEnergySample.dram_or_gpu, currentEnergySample.core, currentEnergySample.package);
	}
	printf("\n -- why does it have 0.000000 some times??? --\n\n");
	fclose(outfile);
}

/////////// JNI Calls Down Here /////////////
/*
static AsyncEnergyMonitor* jniCollector; //managed by JNI function calls
static pthread_t* thread;

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_freeCollector(JNIEnv* env, jclass jcls)
{
	freeAsyncEnergyMonitor(jniCollector);
}

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_startCollecting(JNIEnv* env, jclass jcls, jint samplingRate)
{
	printf("hello w0rld\n");
	jniCollector = newAsyncEnergyMonitor(samplingRate, thread);
	start(jniCollector);
}

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_stopCollecting(JNIEnv* env, jclass jcls)
{
	stop(jniCollector);
	printf("goodbye w0rld\n");
}

JNIEXPORT void Java_jrapl_AsyncEnergyMonitorCSide_writeToFile(JNIEnv* env, jclass jcls, jstring filePath)
{
	writeToFile(jniCollector, (const char*)filePath);
}
*/
