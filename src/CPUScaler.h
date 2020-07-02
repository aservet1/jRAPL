
#include <jni.h>

int ProfileInit();

void EnergyStatCheck(char ener_info[512]);

void ProfileDealloc();


void Java_jrapl_RuntimeTestUtils_FinalizeTimeLogs(JNIEnv *env, jclass jcls);
jint Java_jrapl_JRAPL_ProfileInit(JNIEnv *env, jclass jcls);
jint Java_jrapl_ArchSpec_GetSocketNum(JNIEnv *env, jclass jcls);
jint Java_jrapl_ArchSpec_DramOrGpu(JNIEnv * env, jclass jcls);
jstring Java_jrapl_EnergyCheckUtils_EnergyStatCheck(JNIEnv *env, jclass jcls);
void Java_jrapl_JRAPL_ProfileDealloc(JNIEnv * env, jclass jcls);
