#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "EnergyStats.h"
#include "EnergyCheckUtils.h"
#include "AsyncEnergyMonitor.h"
#include "ArchSpec.h"

void sleep_print(int seconds)
{
	for (int s = 1; s <= seconds; s++) {
		printf("%d\n",s);
		sleep(1);
	}
}

void pkg_power_sampleread() {
	int fd = open("/dev/cpu/0/msr",O_RDONLY);
	rapl_msr_unit rapl_unit = get_rapl_unit(fd);
	double power_pkg = read_msr(fd, MSR_PKG_POWER_INFO) * rapl_unit.power;
	printf("%f\n",power_pkg);
	close(fd);
}

int main(int argc, const char* argv[])
{
	ProfileInit();
	char csv_header[1024];
	energy_stats_csv_header(csv_header);
	printf("%s\n", csv_header);

	char csv_string[1024];
	int num_sockets = getSocketNum();
	EnergyStats stats[num_sockets];
	for (int x = 0; x < 10; x++) {
		sleep(1);
		EnergyStatCheck(stats);
		for (int i = 0; i < num_sockets; i++) {
			EnergyStats e = stats[i];
			energy_stats_csv_string(e, csv_string);
			printf("%s\n", csv_string);
		}
	}
	ProfileDealloc();


	// ProfileInitAllCores(5);
	// ProfileDeallocAllCores();


	// //AsyncEnergyMonitor* m = newAsyncEnergyMonitor(10,DYNAMIC_ARRAY_STORAGE);
	// AsyncEnergyMonitor* m = newAsyncEnergyMonitor(10,LINKED_LIST_STORAGE);
	// start(m);
	// sleep_print(3);
	// //sleep(5);
	// stop(m);
	// writeToFile(m,NULL);

	// int k = 7;
	// EnergyStats* lastk = (EnergyStats*)malloc(sizeof(EnergyStats)*k);
	// //EnergyStats lastk[k];

	// printf("lastk pre-init: %p\n",lastk);
	// lastKSamples(k,m,lastk);
	// printf("lastk pos-init: %p\n",lastk);

	// printf(":)\n --\n");
	// for (int i = 0; i < k; i++) {
	// 	char ener_string[512];
	// 	energy_stats_csv_string(lastk[i],ener_string);
	// 	printf("%s\n",ener_string);
	// }

	// free(lastk); lastk = NULL;
	// freeAsyncEnergyMonitor(m);
}
