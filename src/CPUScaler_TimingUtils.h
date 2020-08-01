#ifndef CPUSCALER_TIMINGUTILS_H
#define CPUSCALER_TIMINGUTILS_H

#include<sys/time.h>

typedef struct {
	const char* name;
	int time;	
} TimeStamp;

typedef struct {
	TimeStamp* array;
	int capacity;
	int nItems;
} TimeLog;

static int timeval_subtract (struct timeval *result, struct timeval *x, struct timeval *y);

static TimeLog* newTimeLog(int initialCapacity);

static void freeTimeLog(TimeLog* t);

static void logTime(TimeLog* log, int time, const char* name);

void printTimeLog(TimeLog* log);

void Java_jrapl_RuntimeTestUtils_CSideTimeProfileInit(JNIEnv *env, jclass jcls, jint iterations);

#endif //CPUSCALER_TIMINGUTILS_H
