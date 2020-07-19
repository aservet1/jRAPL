#include <stdio.h>
#include <unistd.h> 
#include "CPUScaler.h"
#include "EnergyReadingCollector.h"


void sleep_print(int n)
{
	for (int i = 0; i < n; i++){
		sleep(1);
		printf("%d",i);
	}
}

int main(int argc, char* argv[])
{
	printf("hello world\n");

	ProfileInit();

	pthread_t* thread;	
	ReadingCollector* collector = newReadingCollector(1,thread);
	start_collecting(collector);
	sleep(3);
	stop_collecting(collector);
	fileDump(collector,"extra/dump.test");

	ProfileDealloc();
}
