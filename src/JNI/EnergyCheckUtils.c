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

static uint64_t num_sockets;
static int power_domains_supported; // this variable is not necessary to store in this file scope, or is it?

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_profileInit(JNIEnv *env, jclass jcls) {
	num_sockets = getSocketNum();
	power_domains_supported = get_power_domains_supported(get_micro_architecture()); // this variable is not necessary to store in this file scope, or is it?
	ProfileInit();
}

JNIEXPORT jstring JNICALL
Java_jRAPL_NativeAccess_energyStatCheck(JNIEnv *env, jclass jcls) {
	char energy_str[512];
	energy_stat_t energy_stat_per_socket[num_sockets];
	EnergyStatCheck(energy_stat_per_socket);
	energy_stat_csv_string(energy_stat_per_socket, energy_str);
	return (*env)->NewStringUTF(env, energy_str);
}

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_profileDealloc(JNIEnv * env, jclass jcls) {
	ProfileDealloc();
}

JNIEXPORT jstring JNICALL
Java_jRAPL_EnergySample_csvHeader(JNIEnv * env, jclass jcls) {
	char header[512];
	energy_stat_csv_header(header);
 	return (*env)->NewStringUTF(env, header);	
}
