
#ifndef CPUSCALER_H
#define CPUSCALER_H

#include <jni.h>
#include "EnergyStats.h"

#define ALL_SOCKETS 0 // requesting reading for socket 0 means requesting readings for all sockets

void ProfileInit();
void EnergyStatCheck(EnergyStats stats_per_socket[], int whichSocket);
void ProfileDealloc();

void Java_jRAPL_EnergyManager_profileInit(JNIEnv *env, jclass jcls);
jint Java_jRAPL_ArchSpec_getSocketNum(JNIEnv *env, jclass jcls);
jstring Java_jRAPL_EnergyMonitor_energyStatCheck(JNIEnv *env, jclass jcls, jint whichSocket);
void Java_jRAPL_EnergyManager_profileDealloc(JNIEnv * env, jclass jcls);

#endif //CPUSCALER_H
