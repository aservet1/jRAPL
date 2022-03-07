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

static uint64_t num_sockets;

static void copy_to_string(energy_stat_t energy_stat_per_socket[num_sockets], char energy_str[]) {
    int offset = 0;
	for (int i = 0; i < num_sockets; i++) {
    	offset += sprintf(
            energy_str + offset,
            "%.6f#%.6f#%.6f#%.6f#",
    	    energy_stat_per_socket[i].dram,
            energy_stat_per_socket[i].pp0,
    	    energy_stat_per_socket[i].pp1,
            energy_stat_per_socket[i].pkg
        );
    }
    energy_str[strlen(energy_str)-1] = '\0'; // remove the trailing '#'
}

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_profileInit(JNIEnv *env, jclass jcls) {
	num_sockets = getSocketNum();
	ProfileInit();
}

JNIEXPORT jstring JNICALL
Java_jRAPL_NativeAccess_energyStatCheck(JNIEnv *env, jclass jcls) {
	char energy_str[512];
	energy_stat_t energy_stat_per_socket[num_sockets];

    EnergyStatCheck(energy_stat_per_socket);

    copy_to_string(energy_stat_per_socket, energy_str);
    return (*env)->NewStringUTF(env, energy_str);
}

JNIEXPORT void JNICALL
Java_jRAPL_NativeAccess_profileDealloc(JNIEnv * env, jclass jcls) {
	ProfileDealloc();
}
