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



/////////////
JNIEXPORT void JNICALL Java_jRAPL_JavaTest_nativeSandbox(JNIEnv* env, jclass jcls) {
	printf("hello world from sayHello in C\n");
	//EnergyStats e; printf("%.4f %.4f %.4f %.4f\n",e.dram,e.core,e.pkg, e.gpu);//ProfileInit();
}
////////////


static uint64_t num_sockets;

static void copy_to_string(EnergyStats stats_per_socket[num_sockets], char ener_info[512], int which_socket)
{
  	bzero(ener_info, 512);
	int offset = 0;

	char buffer[100];
	int buffer_len;

	int start = (which_socket == ALL_SOCKETS) ? 0 : which_socket-1;
	for (int i = start; i < num_sockets; i++) {
		EnergyStats stats = stats_per_socket[i];
		energy_stats_to_string(stats, buffer);
		buffer_len = strlen(buffer);
		memcpy(ener_info + offset, buffer, buffer_len);
		offset += buffer_len;

		if (which_socket != ALL_SOCKETS) break;
	}
}

JNIEXPORT void JNICALL Java_jRAPL_EnergyManager_profileInit(JNIEnv *env, jclass jcls)
{
	//printf("c-hello1\n");
	uint64_t n = getSocketNum();
	//printf("c-hello2\n");
	num_sockets = n;
	//printf("c-hello3\n");
	ProfileInit();
}

JNIEXPORT jstring JNICALL Java_jRAPL_EnergyMonitor_energyStatCheck(JNIEnv *env, jclass jcls, jint which_socket) {
	
	char ener_info[512];
	EnergyStats stats_per_socket[num_sockets];

	EnergyStatCheck(stats_per_socket, which_socket);
	copy_to_string(stats_per_socket, ener_info, which_socket);
	
	
	jstring ener_string = (*env)->NewStringUTF(env, ener_info);
  	
	return ener_string;

}

JNIEXPORT void JNICALL Java_jRAPL_EnergyManager_profileDealloc(JNIEnv * env, jclass jcls) {

	ProfileDealloc();

}
