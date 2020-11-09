#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include "EnergyStats.h"
#include "AsyncEnergyMonitorCSide.h"

//TODO -- more eloquent error handling
#define assert_valid_id(id)	\
	assert(id >= 0 && id < CAPACITY);

#define CAPACITY 2
static AsyncEnergyMonitor* monitors[CAPACITY];

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_allocMonitor(JNIEnv* env, jclass jcls, jint id, jint samplingRate, jint storageType)
{
	assert_valid_id(id);
	monitors[id] = newAsyncEnergyMonitor(samplingRate, storageType);
}

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_deallocMonitor(JNIEnv* env, jclass jcls, int id)
{
	assert_valid_id(id);
	if (monitors[id] != NULL) {
		freeAsyncEnergyMonitor(monitors[id]);
		monitors[id] = NULL;
	}
}

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_startCollecting(JNIEnv* env, jclass jcls, int id) {
	assert_valid_id(id);
	start(monitors[id]);
}

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_stopCollecting(JNIEnv* env, jclass jcls, int id) {
	assert_valid_id(id);
	stop(monitors[id]);
}

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_writeToFile(JNIEnv* env, jclass jcls, int id, const char* filepath) {
	writeToFile(monitors[id], filepath);
}

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_slightsetup(JNIEnv* env, jclass jcls) {
	for ( int id = 0; id < CAPACITY; id++) {
		monitors[id] = NULL;
	}
}

JNIEXPORT jstring JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_lastKSamples(JNIEnv* env, jclass jcls, int id, int k)
{
	assert( k < monitors[id]->samples_dynarr->nItems );

	EnergyStats samples[k];
	lastKSamples(k, monitors[id], samples);

	char sample_strings[512*(k+1)];
	bzero(sample_strings, 512*(k+1));

	int offset = 0;
	for (int i = 0; i < k; i++) {
		EnergyStats e = samples[i];
		char string[512];
		energy_stats_to_string(e,string);
		sprintf(string,"%s_", string);
		
		int string_len = strlen(string); // +1 for the _ added in %s_
		memcpy(sample_strings + offset, string, string_len);
		offset += string_len;

		//printf("string: %s | s_s:%s | offset:%d\n",string,sample_strings,offset);
	}

	return (*env)->NewStringUTF(env, sample_strings);	
}

JNIEXPORT jlongArray JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_lastKTimestamps(JNIEnv* env, jclass jcls, int id, int k)
{
	assert( k < monitors[id]->samples_dynarr->nItems );

	EnergyStats samples[k];
	lastKSamples(k, monitors[id], samples);

	long fill[k];
	for (int i = 0; i < k; i++)
	{
		struct timeval ts = samples[i].timestamp;
		fill[i] = ts.tv_sec * 1000000 + ts.tv_usec;
	}

	int size = k;
	jlongArray result = (*env)->NewLongArray(env, size);
	(*env)->SetLongArrayRegion(env,result,0,size,fill);

	return result;

}





















