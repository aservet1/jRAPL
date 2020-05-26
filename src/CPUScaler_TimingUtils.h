#ifndef CPUSCALER_TIMINGUTILS_H
#define CPUSCALER_TIMINGUTILS_H

#include<sys/time.h>

typedef struct {
	int length;
	int nItems;
	const char* name;
	int* array;
} TimeLog;

int timeval_subtract (struct timeval *result, struct timeval *x, struct timeval *y);

void initAllLogs(int length);

void finalizeAllLogs();

void logTime(const char* name, int item);

#endif //CPUSCALER_TIMINGUTILS_H
