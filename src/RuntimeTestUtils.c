#include<stdio.h>
#include<sys/time.h>

#include "CPUScaler.h"

/*
jint JNICALL Java_jrapl_JRAPL_ProfileInit(JNIEnv *env, jclass jcls);
jint JNICALL Java_jrapl_ArchSpec_GetSocketNum(JNIEnv *env, jclass jcls);
jstring JNICALL Java_jrapl_EnergyCheckUtils_EnergyStatCheck(JNIEnv *env, jclass jcls);
void JNICALL Java_jrapl_JRAPL_ProfileDealloc(JNIEnv * env, jclass jcls);
*/


static struct timeval start, end, diff;

JNIEXPORT void JNICALL Java_jrapl_RuntimeTestUtils_timeProfileInit(JNIEnv* env, jclass jcls){

	gettimeofday(&start, NULL);

	Java_jrapl_JRAPL_ProfileInit(env, jcls);

	gettimeofday(&end, NULL);
	timersub(&diff, &end, &start);
	printf("ProfileInit(): %ld\n", diff.tv_usec);

}


JNIEXPORT void JNICALL Java_jrapl_RuntimeTestUtils_timeGetSocketNum(JNIEnv* env, jclass jcls){

	gettimeofday(&start, NULL);

	Java_jrapl_ArchSpec_GetSocketNum(env, jcls);

	gettimeofday(&end, NULL);
	timersub(&diff, &end, &start);
	printf("GetSocketNum(): %ld\n", diff.tv_usec);

}
JNIEXPORT void JNICALL Java_jrapl_RuntimeTestUtils_timeEnergyStatCheck(JNIEnv* env, jclass jcls){

	gettimeofday(&start, NULL);

	Java_jrapl_EnergyCheckUtils_EnergyStatCheck(env, jcls);

	gettimeofday(&end, NULL);
	timersub(&diff, &end, &start);
	printf("EnergyStatCheck(): %ld\n", diff.tv_usec);

}
JNIEXPORT void JNICALL Java_jrapl_RuntimeTestUtils_timeProfileDealloc(JNIEnv* env, jclass jcls){

	gettimeofday(&start, NULL);

	Java_jrapl_JRAPL_ProfileDealloc(env, jcls);

	gettimeofday(&end, NULL);
	timersub(&diff, &end, &start);
	printf("ProfileDealloc(): %ld\n", diff.tv_usec);

}
