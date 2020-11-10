#include <stdio.h>
#include <unistd.h>

#include "EnergyStats.h"
#include "CPUScaler.h"
#include "AsyncEnergyMonitorCSide.h"
#include "arch_spec.h"

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

	EnergyStats stats[getSocketNum()];
	EnergyStatCheck(stats,1);
	char ener_string[512];
	energy_stats_to_string(stats[0],ener_string);
	printf("%s\n",ener_string);
	//AsyncEnergyMonitor* m = newAsyncEnergyMonitor(10,DYNAMIC_ARRAY_STORAGE);
	AsyncEnergyMonitor* m = newAsyncEnergyMonitor(10,LINKED_LIST_STORAGE);
	start(m);
	sleep_print(3);
	//sleep(5);
	stop(m);
	writeToFile(m,NULL);
	freeAsyncEnergyMonitor(m);

	ProfileDealloc();
}
