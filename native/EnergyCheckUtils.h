
#ifndef CPUSCALER_H
#define CPUSCALER_H

#include <jni.h>
#include "EnergyStats.h"

void ProfileInit();
void EnergyStatCheck(EnergyStats stats_per_socket[]);
void ProfileDealloc();

void Java_jRAPL_EnergyManager_profileInit(JNIEnv *env, jclass jcls);
jint Java_jRAPL_ArchSpec_getSocketNum(JNIEnv *env, jclass jcls);
jstring Java_jRAPL_EnergyMonitor_energyStatCheck(JNIEnv *env, jclass jcls);
void Java_jRAPL_EnergyManager_profileDealloc(JNIEnv * env, jclass jcls);

#endif //CPUSCALER_H
