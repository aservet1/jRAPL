#include <jni.h>

JNIEXPORT void JNICALL Java_jrapl_RuntimeTestUtils_StartTimeLogs(JNIEnv *env, jclass jcls, jint logSize, jboolean _timingFunctionCalls, jboolean _timingMsrReadings);
JNIEXPORT void JNICALL Java_jrapl_RuntimeTestUtils_FinalizeTimeLogs(JNIEnv *env, jclass jcls);
JNIEXPORT jint JNICALL Java_jrapl_JRAPL_ProfileInit(JNIEnv *env, jclass jcls);
JNIEXPORT jint JNICALL Java_jrapl_ArchSpec_GetSocketNum(JNIEnv *env, jclass jcls);
JNIEXPORT jint JNICALL Java_jrapl_ArchSpec_DramOrGpu(JNIEnv * env, jclass jcls);
JNIEXPORT jstring JNICALL Java_jrapl_EnergyCheckUtils_EnergyStatCheck(char ener_info[], JNIEnv *env, jclass jcls);
JNIEXPORT void JNICALL Java_jrapl_JRAPL_ProfileDealloc(JNIEnv * env, jclass jcls);
