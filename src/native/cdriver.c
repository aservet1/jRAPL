#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "energy_check_utils.h"
#include "async_energy_monitor.h"
#include "arch_spec.h"

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

	AsyncEnergyMonitor* m = newAsyncEnergyMonitor(10,DYNAMIC_ARRAY_STORAGE,64);
	// AsyncEnergyMonitor* m = newAsyncEnergyMonitor(10,LINKED_LIST_STORAGE,64);
	start(m);
	sleep_print(3);
	//sleep(5);
	stop(m);
	writeFileCSV(m,NULL);

	int k = 7;
	//energy_info_t* lastk = (energy_info_t*)malloc(sizeof(energy_info_t)*k);
	energy_measurement_t lastk[k];

	printf("lastk pre-init: %p\n",lastk);
	lastKSamples(k,m,lastk);
	printf("lastk pos-init: %p\n",lastk);

	printf(":)\n --\n");
	char csv_string_buffer[512];
	int num_sockets = getSocketNum();
	energy_info_t multisocket_sample_buffer[num_sockets];
	for (int i = 0; i < k; i+=num_sockets) {
		for (int j = 0; j < num_sockets; j++) {
			multisocket_sample_buffer[j] = lastk[i+j];
		}
		energy_stats_csv_string(multisocket_sample_buffer, csv_string_buffer);
		printf("%s\n", csv_string_buffer);
	}

	//free(lastk); lastk = NULL;
	freeAsyncEnergyMonitor(m);

	ProfileDealloc();

	// ProfileInit();
	// char csv_header[1024];
	// energy_stats_csv_header(csv_header);
	// printf("%s\n", csv_header);

	// char csv_string[1024];
	// int num_sockets = getSocketNum();
	// energy_stat_t stats[num_sockets];
	// for (int x = 0; x < 10; x++) {
	// 	sleep(1);
	// 	EnergyStatCheck(stats);
	// 	for (int i = 0; i < num_sockets; i++) {
	// 		energy_info_t e = stats[i];
	// 		energy_stats_csv_string(e, csv_string);
	// 		printf("%s\n", csv_string);
	// 	}
	// }
	// ProfileDealloc();

	// ProfileInitAllCores(5);
	// ProfileDeallocAllCores();

}
