#include <stdio.h>
#include <unistd.h> // for sleep()
#include "CPUScaler.h"

#define filler NULL, NULL

int main(int argc, char* argv[])
{
	printf("hello world\n");

	ProfileInit();
	char ener_info[512];
	for (int i = 0; i < 100; i++) {
		EnergyStatCheck(ener_info);
		sleep(1);
		printf("%s\n",ener_info);
	}
	ProfileDealloc();

}
