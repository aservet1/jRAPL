#include <stdio.h>
#include <jni.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
#include <stdint.h>
#include <string.h>
#include <inttypes.h>
#include <sys/types.h>

#include "energy_check_utils.h"
#include "arch_spec.h"
#include "msr.h"

#include "JNIFunctionDeclarations.h"

// #define MSR_DRAM_ENERGY_UNIT 0.000015 // this one is not necessary to define in this file scope, is it?

static uint64_t num_sockets;
static int power_domains_supported; // this variable is not necessary to store in this file scope, or is it?

JNIEXPORT void JNICALL
Java_jRAPL_RaplSingleton_profileInit(JNIEnv *env, jclass jcls) {
	num_sockets = getSocketNum();
	power_domains_supported = get_power_domains_supported(get_micro_architecture()); // this variable is not necessary to store in this file scope, or is it?
	ProfileInit();
}

JNIEXPORT jstring JNICALL
Java_jRAPL_RaplSingleton_energyStatCheck(JNIEnv *env, jclass jcls) {
	char ener_info[512];
	EnergyStats stats_per_socket[num_sockets];
	EnergyStatCheck(stats_per_socket);
	energy_stats_csv_string(stats_per_socket, ener_info);
	return (*env)->NewStringUTF(env, ener_info);
}

JNIEXPORT void JNICALL
Java_jRAPL_RaplSingleton_profileDealloc(JNIEnv * env, jclass jcls) {
	ProfileDealloc();
}

JNIEXPORT jstring JNICALL
Java_jRAPL_EnergySample_csvHeader(JNIEnv * env, jclass jcls) {
	char header[512];
	energy_stats_csv_header(header);
 	return (*env)->NewStringUTF(env, header);	
}
