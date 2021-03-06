#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include "ArchSpec.h"
#include "EnergyStats.h"
#include "AsyncEnergyMonitor.h"
#include "Utils.h"

static AsyncEnergyMonitor* monitor = NULL;

JNIEXPORT void JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_activateNative(JNIEnv* env, jclass jcls, jint samplingRate, jint storageType, jint initialSize) {
	monitor = newAsyncEnergyMonitor(samplingRate, storageType, initialSize);
}

JNIEXPORT void JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_deactivateNative(JNIEnv* env, jclass jcls) {
	if (monitor != NULL) {
		freeAsyncEnergyMonitor(monitor);
		monitor = NULL;
	}
}

JNIEXPORT void JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_startNative(JNIEnv* env, jclass jcls) {
	start(monitor);
}

JNIEXPORT void JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_stopNative(JNIEnv* env, jclass jcls) {
	stop(monitor);
}

JNIEXPORT void JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_resetNative(JNIEnv* env, jclass jcls) {
	reset(monitor);
}

JNIEXPORT void JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_writeFileCSVNative(JNIEnv* env, jclass jcls, jstring jstringFilepath) {
	const char* filepath = (*env)->GetStringUTFChars(env, jstringFilepath, NULL);
	writeFileCSV(monitor, filepath);
	(*env)->ReleaseStringUTFChars(env, jstringFilepath, filepath);
}

JNIEXPORT jstring JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_getLastKSamplesNative(JNIEnv* env, jclass jcls, int k) {
	if (monitor->samples_dynarr) assert( k <= monitor->samples_dynarr->nItems );
	if (monitor->samples_linklist) assert( k <= monitor->samples_linklist->nItems );

	size_t num_sockets = getSocketNum();

	k *= num_sockets;

	EnergyStats samples[k];
	lastKSamples(k, monitor, samples);

	char sample_strings[512*k];
	bzero(sample_strings, 512*k);

	char csv_string[512];
	EnergyStats multisocket_sample_buffer[num_sockets];

	int offset = 0;
	for (int i = 0; i < k; i+=num_sockets) {

		for (int j = 0; j < num_sockets; j++)
			multisocket_sample_buffer[j] = samples[i+j];
		energy_stats_csv_string(multisocket_sample_buffer, csv_string);

		offset += sprintf(sample_strings + offset, "%s_", csv_string);

	}
	return (*env)->NewStringUTF(env, sample_strings);	
}

JNIEXPORT jlongArray JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_getLastKTimestampsNative(JNIEnv* env, jclass jcls, int k) {
	EnergyStats samples[k];
	lastKSamples(k, monitor, samples);

	long fill[k];
	for (int i = 0; i < k; i++) fill[i] = samples[i].timestamp;

	int size = k;
	jlongArray result = (*env)->NewLongArray(env, size);
	(*env)->SetLongArrayRegion(env,result,0,size,fill);

	return result;
}

JNIEXPORT jint JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_getNumSamplesNative(JNIEnv* env, jclass jcls) {
	return (jint)getNumSamples(monitor);
}

JNIEXPORT void JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_setSamplingRateNative(JNIEnv* env, jclass jcls, jint s) {
	setSamplingRate(monitor,(int)s);
}

JNIEXPORT jint JNICALL
Java_jRAPL_AsyncEnergyMonitorCSide_getSamplingRateNative(JNIEnv* env, jclass jcls) {
	return getSamplingRate(monitor);
}
