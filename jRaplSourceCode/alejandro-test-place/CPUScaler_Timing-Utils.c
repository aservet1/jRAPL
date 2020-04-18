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

TimeLog* initTimeLog(const int length, const char* functionName)
{
	TimeLog* a = (TimeLog*)malloc(sizeof(TimeLog));
	a->length = length;
	a->nItems = 0;
	a->functionName = functionName;
	a->array = (int*)malloc(sizeof(int)*length);
	for (int i = 0; i < a->length; i++)
		a->array[i] = -1;
	return a;
}

void logItem(TimeLog* tl, int item)
{
	assert(tl->nItems < tl->length && item >= 0);
	tl->array[tl->nItems++] = item;
}

void freeTimeLog(TimeLog* a)
{
	free(a->array);
	free(a);
}


void printTimeLog(TimeLog* tl)
{
	for (int i = 0; i < tl->length; i++) {
		int item = tl->array[i];
		assert(item >= 0);
		printf("%s: %d\n",tl->functionName, item);
	}
}

void print_and_free(TimeLog* timeLogs[], int nLogs)
{
	for (int i = 0; i < nLogs; i++)
	{
		TimeLog* current = timeLogs[i];
		printTimeLog(current);
		freeTimeLog(current);
	}
}

int main(int argc, char *argv[])
{
	TimeLog* a = initTimeLog(10,"TimeLogA");
	TimeLog* b = initTimeLog(100,"TimeLogB");

	for (int i = 10; i >= 1; i--)
		logItem(a,i);
	for (int i = 0; i < b->length; i++)
		logItem(b,i*256);

	TimeLog* logs[] = {a,b};
	print_and_free(logs, 2);

	return 0;
}
