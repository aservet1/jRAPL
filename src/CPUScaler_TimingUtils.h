#ifndef CPUSCALER_TIMINGUTILS_H
#define CPUSCALER_TIMINGUTILS_H

#include<sys/time.h>

typedef struct {
	int length;
	int nItems;
	const char* name;
	int* array;
} TimeLog;

static int timeval_subtract (struct timeval *result, struct timeval *x, struct timeval *y);

void initAllLogs(int length);

void finalizeAllLogs();

static void logTime(const char* name, int item);

void Java_jrapl_RuntimeTestUtils_CSideTimeProfileInit(JNIEnv *env, jclass jcls, jint iterations);

#endif //CPUSCALER_TIMINGUTILS_H
