
#ifndef CPUSCALER_H
#define CPUSCALER_H

#include <jni.h>
#include "EnergyStats.h"

void ProfileInit();
void EnergyStatCheck(EnergyStats stats_per_socket[]);
void ProfileDealloc();

void Java_jrapl_JRAPL_ProfileInit(JNIEnv *env, jclass jcls);
jint Java_jrapl_ArchSpec_GetSocketNum(JNIEnv *env, jclass jcls);
jint Java_jrapl_ArchSpec_DramOrGpu(JNIEnv * env, jclass jcls);
jstring Java_jrapl_EnergyMonitor_EnergyStatCheck(JNIEnv *env, jclass jcls);
void Java_jrapl_JRAPL_ProfileDealloc(JNIEnv * env, jclass jcls);

#endif //CPUSCALER_H
