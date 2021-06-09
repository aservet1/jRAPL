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

#include "JNIFunctionDeclarations.h"
#include "EnergyCheckUtils.h"
#include "ArchSpec.h"
#include "MSR.h"
#include "EnergyStats.h"

#define MSR_DRAM_ENERGY_UNIT 0.000015 // this one is not necessary to define in this file scope, is it?

static uint64_t num_sockets;
static int power_domains_supported; // this variable is not necessary to store in this file scope, or is it?

static void
copy_to_string(EnergyStats stats_per_socket[num_sockets], char ener_info[512]) {
	for (int i = 0; i < num_sockets; i++) {
		EnergyStats stats = stats_per_socket[i];
		ener_info += energy_stats_to_jni_string(stats, ener_info);
	}
}

JNIEXPORT void JNICALL
Java_jRAPL_EnergyManager_profileInit(JNIEnv *env, jclass jcls) {
	num_sockets = getSocketNum();
	power_domains_supported = get_power_domains_supported(get_micro_architecture()); // this variable is not necessary to store in this file scope, or is it?
	ProfileInit();
}

JNIEXPORT jstring JNICALL
Java_jRAPL_EnergyMonitor_energyStatCheck(JNIEnv *env, jclass jcls) {
	char ener_info[512];
	EnergyStats stats_per_socket[num_sockets];
	EnergyStatCheck(stats_per_socket);
	copy_to_string(stats_per_socket, ener_info);
	return (*env)->NewStringUTF(env, ener_info);
}

JNIEXPORT void JNICALL
Java_jRAPL_EnergyManager_profileDealloc(JNIEnv * env, jclass jcls) {
	ProfileDealloc();
}
