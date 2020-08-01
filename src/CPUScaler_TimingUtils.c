#include<stdio.h>
#include<stdlib.h>
#include<assert.h>
#include<stdbool.h>
#include<string.h>
#include<jni.h>
#include "CPUScaler_TimingUtils.h"
#include "CPUScaler.h"

static TimeLog* newTimeLog(int initialCapacity)
{
	TimeLog* t = (TimeLog*)malloc(sizeof(TimeLog));
	t->array = (TimeStamp*)malloc(sizeof(TimeStamp)*initialCapacity);
	t->capacity = initialCapacity;
	t->nItems = 0;
	return t;
}

static void freeTimeLog(TimeLog* t)
{
	free(t->array);
	free(t);
}

static void increaseCapacity(TimeLog* log)
{
	log->capacity = log->capacity * 2;
	log->array = (TimeStamp*)realloc(log->array, sizeof(TimeStamp)*(log->capacity));
}

static void logTime(TimeLog* log, int time, const char* name)
{
	TimeStamp stamp;
	stamp.name = name;
	stamp.time = time;

	bool isFull = log->nItems >= log->capacity;
	if (isFull) increaseCapacity(log);
	log->array[log->nItems++] = stamp;
	printf("%d ",log->nItems);
}

void printTimeLog(TimeLog* log)
{
	for (int i = 0; i < log->nItems; i++) {
		TimeStamp current = log->array[i];
		printf("%s:%d\n",current.name,current.time);
	}
}

/*
#include <time.h>

int main(int argc, char *argv[])
{
	srand(time(0)); char* options[] = {"ProfileInit()","GetSocketNum()","EnergyStatCheck()","ProfileDealloc()","DRAM","PACKAGE","CORE","GPU"}; int options_length = 8;

	TimeLog* log = newTimeLog(16); //@TODO make static global
	for (int x = 0; x < 100; x++) logTime(log, rand()%1000,options[rand()%options_length]);

	//printf("capacity: %d, nItems: %d\n",log->capacity,log->nItems);
	printTimeLog(log);	

	freeTimeLog(log);

	return 0;
}
*/
