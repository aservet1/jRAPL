#include <stdio.h>
#include <unistd.h> 
#include <stdlib.h>

#include "CPUScaler.h"
#include "EnergyStats.h"

int main(int argc, const char* argv[])
{
	ProfileInit();
	EnergyStats e[1];
	EnergyStatCheck(e);
	char ener_string[100];
	energy_stats_to_string(e[0],ener_string);
	printf("%s\n", ener_string);
	ProfileDealloc();
}
