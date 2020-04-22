#include<stdio.h>
#include<stdlib.h>
#include<assert.h>
#include<stdbool.h>
#include<string.h>

#include "CPUScaler_TimingUtils.h"

//@TODO document all of this

//time logs for function runtime
static TimeLog* ProfileInitLog = NULL;
static TimeLog* GetSocketNumLog = NULL;
static TimeLog* EnergyStatCheckLog = NULL;
static TimeLog* ProfileDeallocLog = NULL;

//time logs for msr read runtime
static TimeLog* dramSocket0Log = NULL;
static TimeLog* packageSocket0Log = NULL;
static TimeLog* coreSocket0Log = NULL;
static TimeLog* gpuSocket0Log = NULL;
static TimeLog* dramSocket1Log = NULL;
static TimeLog* packageSocket1Log = NULL;
static TimeLog* coreSocket1Log = NULL;
static TimeLog* gpuSocket1Log = NULL;


int timeval_subtract (struct timeval *result, struct timeval *x, struct timeval *y)
{
  /// Perform the carry for the later subtraction by updating y.
  if (x->tv_usec < y->tv_usec) {
    int nsec = (y->tv_usec - x->tv_usec) / 1000000 + 1;
    y->tv_usec -= 1000000 * nsec;
    y->tv_sec += nsec;
  }
  if (x->tv_usec - y->tv_usec > 1000000) {
    int nsec = (x->tv_usec - y->tv_usec) / 1000000;
    y->tv_usec += 1000000 * nsec;
    y->tv_sec -= nsec;
  }

  // Compute the time remaining to wait. tv_usec is certainly positive.
  result->tv_sec = x->tv_sec - y->tv_sec;
  result->tv_usec = x->tv_usec - y->tv_usec;

  // Return 1 if result is negative.
  return x->tv_sec < y->tv_sec;
}

static TimeLog* initTimeLog(const int length, const char* name)
{
	TimeLog* tl = (TimeLog*)malloc(sizeof(TimeLog));
	tl->length = length;
	tl->nItems = 0;
	tl->name = name;
	tl->array = (int*)malloc(sizeof(int)*length);
	for (int i = 0; i < tl->length; i++)
		tl->array[i] = -1;
	return tl;
}

static void freeTimeLog(TimeLog* a)
{
	free(a->array);
	free(a);
}

static void printTimeLog(TimeLog* tl)
{
	for (int i = 0; i < tl->length; i++) {
		int item = tl->array[i];
		const char* name = tl->name;
		if(item >= 0)
			printf("%s: %d\n", name, item);
	}
}

void initAllLogs(const int length)
{
	ProfileInitLog = initTimeLog(length, "ProfileInit()");
	GetSocketNumLog = initTimeLog(length, "GetSocketNum()");
	EnergyStatCheckLog = initTimeLog(length, "EnergyStatCheck()");
	ProfileDeallocLog = initTimeLog(length, "ProfileDealloc()");
	
	dramSocket0Log = initTimeLog(length,"DRAM Socket0");
	packageSocket0Log = initTimeLog(length,"PACKAGE Socket0");
	coreSocket0Log = initTimeLog(length,"CORE Socket0");
	gpuSocket0Log = initTimeLog(length,"GPU Socket0");

	dramSocket1Log = initTimeLog(length,"DRAM Socket1");
	packageSocket1Log = initTimeLog(length,"PACKAGE Socket1");
	coreSocket1Log = initTimeLog(length,"CORE Socket1");
	gpuSocket1Log = initTimeLog(length,"GPU Socket1");
}

void finalizeAllLogs()
{
	TimeLog* allLogs[]= {ProfileInitLog, GetSocketNumLog, EnergyStatCheckLog, ProfileDeallocLog, 
							dramSocket0Log, packageSocket0Log, coreSocket0Log, gpuSocket0Log,
							dramSocket1Log, packageSocket1Log, coreSocket1Log, gpuSocket1Log};
	int nLogs = 12;
	for (int i = 0; i < nLogs; i++) {
		if (allLogs[i] != NULL) {
			printTimeLog(allLogs[i]);
			freeTimeLog(allLogs[i]);
		}
	}
}


void logTime(const char* name, int item)
{
	TimeLog* tl;
	if 		(!strcmp(name,"ProfileInit()")) tl = ProfileInitLog;
	else if (!strcmp(name,"GetSocketNum()")) tl = GetSocketNumLog;
	else if (!strcmp(name,"EnergyStatCheck()")) tl = EnergyStatCheckLog;
	else if (!strcmp(name,"ProfileDealloc()")) tl = ProfileDeallocLog;
	
	else if (!strcmp(name,"DRAM Socket 0")) tl = dramSocket0Log;
	else if (!strcmp(name,"PACKAGE Socket 0")) tl = packageSocket0Log;
	else if (!strcmp(name,"CORE Socket 0")) tl = coreSocket0Log;
	else if (!strcmp(name,"GPU Socket 0")) tl = gpuSocket0Log;

	else if (!strcmp(name,"DRAM Socket 1")) tl = dramSocket1Log;
	else if (!strcmp(name,"PACKAGE Socket 1")) tl = packageSocket1Log;
	else if (!strcmp(name,"CORE Socket 1")) tl = coreSocket1Log;
	else if (!strcmp(name,"GPU Socket 1")) tl = gpuSocket1Log;

	else {
		printf("ERROR: invalid log name: %s\n",name);
		exit(1);
	}

	if (tl->nItems < tl->length)
		tl->array[tl->nItems++] = item;
}

/*#include <time.h>

int main(int argc, char *argv[])
{
	srand(time(0));
	char* options[] = {"ProfileInit","GetSocketNum","EnergyStatCheck","ProfileDealloc","DRAM","PACKAGE","CORE","GPU"};
	int options_length = 8;

	initAllLogs(20);

	for (int x = 0; x < 100; x++) {
		logTime(rand()%1000,options[rand()%options_length]);
	}
	
	
	finalizeAllLogs();

	return 0;
}*/
