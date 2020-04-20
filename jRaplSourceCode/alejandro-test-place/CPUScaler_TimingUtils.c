#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<assert.h>

typedef struct {
	int length;
	int nItems;
	const char* functionName;
	int* array;
}TimeLog;

static TimeLog* ProfileInitLog;
static TimeLog* GetSocketNumLog;
static TimeLog* EnergyStatCheckLog;
static TimeLog* ProfileDeallocLog;
//@TODO need to implement MSR logs...

static int timeval_subtract (struct timeval *result, struct timeval *x, struct timeval *y)
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

static TimeLog* initTimeLog(const int length, const char* functionName)
{
	TimeLog* tl = (TimeLog*)malloc(sizeof(TimeLog));
	tl->length = length;
	tl->nItems = 0;
	tl->functionName = functionName;
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
		if(item >= 0) printf("%s: %d\n",tl->functionName, item);
	}
}

void initAllLogs(const int length)
{
	ProfileInitLog = initTimeLog(length, "ProfileInit");
	GetSocketNumLog = initTimeLog(length, "GetSocketNum");
	EnergyStatCheckLog = initTimeLog(length, "EnergyStatCheck");
	ProfileDeallocLog = initTimeLog(length, "ProfileDealloc");
}

void finalizeAllLogs()
{
	TimeLog* allLogs[]= {ProfileInitLog, GetSocketNumLog, EnergyStatCheckLog, ProfileDeallocLog};
	int nLogs = 4;
	for (int i = 0; i < nLogs; i++)
	{
		printTimeLog(allLogs[i]);
		freeTimeLog(allLogs[i]);
	}
}

void logItem(const char* functionName, int item)
{
	TimeLog* tl;
	if (!strcmp(functionName,"ProfileInit")) tl = ProfileInitLog;
	if (!strcmp(functionName,"GetSocketNum")) tl = GetSocketNumLog;
	if (!strcmp(functionName,"EnergyStatCheck")) tl = EnergyStatCheckLog;
	if (!strcmp(functionName,"ProfileDealloc")) tl = ProfileDeallocLog;
	assert(tl->nItems < tl->length && item >= 0);
	tl->array[tl->nItems++] = item;
}

int main(int argc, char *argv[])
{
	initAllLogs(40);
	finalizeAllLogs();

	return 0;
}
