
#include <stdio.h>
#include <jni.h>
#include <assert.h>
#include "AsyncEnergyMonitorCSide.h"
#include "EnergyStats.h"

//TODO -- more eloquent error handling
#define assert_valid_id(id)	\
	assert(id >= 0 && id < capacity);

static const int capacity = 2;
static AsyncEnergyMonitor* monitors[2];

JNIEXPORT void JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_allocMonitor(JNIEnv* env, jclass jcls, int id, int samplingRate)
{
	assert_valid_id(id);
	monitors[id] = newAsyncEnergyMonitor(samplingRate);
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
	//printf(" -- id:%d -- cap:%d -- ptr:%p\n",id,capacity,monitors[id]);
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
	for ( int id = 0; id < capacity; id++) {
		monitors[id] = NULL;
	}
}


JNIEXPORT jstring JNICALL
Java_jrapl_AsyncEnergyMonitorCSide_lastKSamples(JNIEnv* env, jclass jcls, int id, int k) {
	/*
	printf("k: %d ; nItems: %lld\n",k,monitors[id]->samples->nItems);

	assert( k < monitors[id]->samples->nItems );

	//	printf("k1:%d|",k);
	EnergyStats samples[k];
	//	printf("k2:%d|",k);
	lastKSamples(k, monitors[id], samples);
	//	printf("k3:%d\n",k);

	for (int i = 0; i < 3; i++) {
		printf("loop (%d,%d). ",i,k);
		printf("%d{%f{%f ", samples[i].socket, samples[i].dram, samples[i].gpu);
	}
	*/
	const char* message = "hello mo0n";
	return (*env)->NewStringUTF(env, message);	
}







