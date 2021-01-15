#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include "EnergyStats.h"
#include "AsyncEnergyMonitorCSide.h"


static AsyncEnergyMonitor* monitor;


JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_allocMonitor(JNIEnv* env, jclass jcls, jint samplingRate, jint storageType)
{
	monitor = newAsyncEnergyMonitor(samplingRate, storageType);
}

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_deallocMonitor(JNIEnv* env, jclass jcls)
{
	if (monitor != NULL) {
		freeAsyncEnergyMonitor(monitor);
		monitor = NULL;
	}
}

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_startCollecting(JNIEnv* env, jclass jcls) {
	start(monitor);
}

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_stopCollecting(JNIEnv* env, jclass jcls) {
	stop(monitor);
}

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_cSideReset(JNIEnv* env, jclass jcls) {
	reset(monitor);
}

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_writeToFileFromC(JNIEnv* env, jclass jcls, jstring jstringFilepath) {
	const char* filepath = (*env)->GetStringUTFChars(env, jstringFilepath, NULL);
	writeToFile(monitor, filepath);
	(*env)->ReleaseStringUTFChars(env, jstringFilepath, filepath);
}

JNIEXPORT jstring JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_lastKSamples(JNIEnv* env, jclass jcls, int k)
{
	if (monitor->samples_dynarr) assert( k < monitor->samples_dynarr->nItems );
	if (monitor->samples_linklist) assert( k < monitor->samples_linklist->nItems );

	EnergyStats samples[k];
	//EnergyStats* samples = (EnergyStats*)malloc(sizeof(EnergyStats)*k);
	
	lastKSamples(k, monitor, samples);

	char sample_strings[512*(k+1)];
	bzero(sample_strings, 512*(k+1));

	int offset = 0;
	for (int i = 0; i < k; i++) {
		EnergyStats e = samples[i];
		char string[512];
		energy_stats_to_string(e,string);
		char string2[512+10];
		sprintf(string2,"%s_", string);
		
		int string_len = strlen(string2); // +1 for the _ added in %s_
		memcpy(sample_strings + offset, string2, string_len);
		offset += string_len;

	}
	//free(samples);
	return (*env)->NewStringUTF(env, sample_strings);	
}

JNIEXPORT jlongArray JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_lastKTimestamps(JNIEnv* env, jclass jcls, int k)
{
	EnergyStats samples[k];
	lastKSamples(k, monitor, samples);

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


