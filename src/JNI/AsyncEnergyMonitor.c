#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include "arch_spec.h"
#include "async_energy_monitor.h"
#include "energy_check_utils.h"
#include "utils.h"

static AsyncEnergyMonitor* monitor = NULL;

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_activateMonitor(JNIEnv* env, jclass jcls, jint samplingRate, jint storageType, jint initialSize) {
	monitor = newAsyncEnergyMonitor(samplingRate, storageType, initialSize);
}

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_deactivateMonitor(JNIEnv* env, jclass jcls) {
	if (monitor != NULL) {
		freeAsyncEnergyMonitor(monitor);
		monitor = NULL;
	}
}

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_startMonitor(JNIEnv* env, jclass jcls) {
	start(monitor);
}

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_stopMonitor(JNIEnv* env, jclass jcls) {
	stop(monitor);
}

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_resetMonitor(JNIEnv* env, jclass jcls) {
	reset(monitor);
}

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_writeFileCSVMonitor(JNIEnv* env, jclass jcls, jstring jstringFilepath) {
	const char* filepath = (*env)->GetStringUTFChars(env, jstringFilepath, NULL);
	writeFileCSV(monitor, filepath);
	(*env)->ReleaseStringUTFChars(env, jstringFilepath, filepath);
}

JNIEXPORT jstring JNICALL
Java_jRAPL_NativeAccess_getLastKSamplesMonitor(JNIEnv* env, jclass jcls, int k) {
	if (monitor->samples_dynarr)   assert( k <= monitor->samples_dynarr->nItems );
	if (monitor->samples_linklist) assert( k <= monitor->samples_linklist->nItems );

	size_t num_sockets = getSocketNum();

	k *= num_sockets;

	energy_measurement_t samples[k];
	lastKSamples(k, monitor, samples);

	char sample_strings[512*k];
	bzero(sample_strings, 512*k);

	char csv_string[512];
	energy_measurement_t energy_measurement_per_socket[num_sockets];

	int offset = 0;
	for (int i = 0; i < k; i+=num_sockets) {
		for (int j = 0; j < num_sockets; j++) {
			energy_measurement_per_socket[j] = samples[i+j];
		} energy_measurement_csv_string(energy_measurement_per_socket, csv_string);
		offset += sprintf(sample_strings + offset, "%s_", csv_string);
	}
	return (*env)->NewStringUTF(env, sample_strings);	
}

// JNIEXPORT jlongArray JNICALL
// Java_jRAPL_NativeAccess_getLastKTimestampsMonitor(JNIEnv* env, jclass jcls, int k) {
// 	energy_measurement_t measurements[k];
// 	lastKSamples(k, monitor, measurements);
// 
// 	long fill[k];
// 	for (int i = 0; i < k; i++) fill[i] = measurements[i].time_elapsed;
// 
// 	int size = k;
// 	jlongArray result = (*env)->NewLongArray(env, size);
// 	(*env)->SetLongArrayRegion(env,result,0,size,fill);
// 
// 	return result;
// }

JNIEXPORT jint JNICALL
Java_jRAPL_NativeAccess_getNumSamplesMonitor(JNIEnv* env, jclass jcls) {
	return (jint)getNumSamples(monitor);
}

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_setSamplingRateMonitor(JNIEnv* env, jclass jcls, jint s) {
	setSamplingRate(monitor,(int)s);
}

JNIEXPORT jint JNICALL
Java_jRAPL_NativeAccess_getSamplingRateMonitor(JNIEnv* env, jclass jcls) {
	return getSamplingRate(monitor);
}
