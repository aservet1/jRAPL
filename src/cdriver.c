#include <stdio.h>
#include <unistd.h> 
#include "CPUScaler.h"
#include "AsyncEnergyMonitorCSide.h"

int fib(int n)
{
	if (n <= 1) return 1;
	else return fib(n-1) + fib(n-2);
}

void sleep_print(int n)
{
	for (int i = 0; i < n; i++){
		sleep(1);
		printf("%d",i);
	}
}

int main(int argc, const char* argv[])
{
	printf("hello world\n");

	ProfileInit();

	pthread_t* thread;	
	ReadingCollector* collector = newReadingCollector(0, thread);
	start_collecting(collector);
	//fib(42); //take up some time
	sleep(5);
	stop_collecting(collector);
	fileDump(collector, (argc>1)?argv[1]:NULL );
	freeReadingCollector(collector);

	ProfileDealloc();
}
