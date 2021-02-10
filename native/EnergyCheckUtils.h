
#ifndef ENERGY_CHECK_UTILS_H
#define ENERGY_CHECK_UTILS_H

#include <jni.h>
#include "EnergyStats.h"

void ProfileInit();
void EnergyStatCheck(EnergyStats stats_per_socket[]);
void ProfileDealloc();

void Java_jRAPL_EnergyManager_profileInit(JNIEnv *env, jclass jcls);
jint Java_jRAPL_ArchSpec_getSocketNum(JNIEnv *env, jclass jcls);
jstring Java_jRAPL_EnergyMonitor_energyStatCheck(JNIEnv *env, jclass jcls);
void Java_jRAPL_EnergyManager_profileDealloc(JNIEnv * env, jclass jcls);

int rapl_unit_fd();
int* get_msr_fds();

#endif //ENERGY_CHECK_UTILS_H
