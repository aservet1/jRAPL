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

//////////////////////\\\\\\\\\\\\\\\\\\\\
//// DO NOT DELETE WHAT IS IN THIS     \\\\
 //// COMMENT SECTION SAVE IT TO THE    \\\\
  //// JAVA THREAD TEST DRIVER FILE WHEN \\\\
   //// YOU GET THE OPPORTUNITY           \\\\
   ///////////////////////\\\\\\\\\\\\\\\\\\\\\

void run_cthread(int argc, const char* argv[])
{
	printf("hello world\n");

	pthread_t thread;	
	ReadingCollector* collector = newReadingCollector(100, thread);
	start_collecting(collector);
	//fib(42); //take up some time
	sleep(5);
	stop_collecting(collector);
	fileDump(collector, (argc>1)?argv[1]:NULL );
	freeReadingCollector(collector);

}
/////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

int main(int argc, const char* argv[])
{

	ProfileInit();

	run_cthread(argc,argv);

	ProfileDealloc();
}
