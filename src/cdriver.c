#include <stdio.h>
#include <unistd.h>

#include "CPUScaler.h"
#include "AsyncEnergyMonitorCSide.h"

void sleep_print(int seconds)
{
	for (int s = 1; s <= seconds; s++) {
		printf("%d\n",s);
		sleep(1);
	}
}

int main(int argc, const char* argv[])
{
	ProfileInit();

	AsyncEnergyMonitor* m = newAsyncEnergyMonitor(10,DYNAMIC_ARRAY_STORAGE);
	//AsyncEnergyMonitor* m = newAsyncEnergyMonitor(10,LINKED_LIST_STORAGE);
	start(m);
	//sleep_print(3);
	sleep(5);
	stop(m);
	writeToFile(m,NULL);
	freeAsyncEnergyMonitor(m);

	ProfileDealloc();
}
