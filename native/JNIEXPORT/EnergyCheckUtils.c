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

#include "EnergyCheckUtils.h"
#include "ArchSpec.h"
#include "MSR.h"
#include "EnergyStats.h"

#define MSR_DRAM_ENERGY_UNIT 0.000015

static uint64_t num_sockets;
static int power_domains_supported;

static void copy_to_string(EnergyStats stats_per_socket[num_sockets], char ener_info[512])
{
	for (int i = 0; i < num_sockets; i++) {
		EnergyStats stats = stats_per_socket[i];
		ener_info += energy_stats_to_string(stats, ener_info, power_domains_supported);
	}
}

JNIEXPORT void JNICALL Java_jRAPL_EnergyManager_profileInit(JNIEnv *env, jclass jcls)
{
	num_sockets = getSocketNum();
	power_domains_supported = get_power_domains_supported(get_cpu_model(),NULL);
	ProfileInit();
}

JNIEXPORT jstring JNICALL Java_jRAPL_EnergyMonitor_energyStatCheck(JNIEnv *env, jclass jcls) {
	
	char ener_info[512];
	EnergyStats stats_per_socket[num_sockets];

	EnergyStatCheck(stats_per_socket);
	copy_to_string(stats_per_socket, ener_info);
	
	
	jstring ener_string = (*env)->NewStringUTF(env, ener_info);
  	
	return ener_string;

}

JNIEXPORT void JNICALL Java_jRAPL_EnergyManager_profileDealloc(JNIEnv * env, jclass jcls) {

	ProfileDealloc();

}
