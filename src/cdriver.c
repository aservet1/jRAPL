#include <stdio.h>
#include <unistd.h> // for sleep()
#include <stdlib.h>
#include "CPUScaler.h"
#include "CPUScaler_TimingUtils.h"
#define filler NULL, NULL

int main(int argc, char* argv[])
{
	int iterations = atoi(argv[1]);

	ProfileInit();

	initAllLogs(iterations);
	Java_jrapl_RuntimeTestUtils_CSideTimeProfileInit(filler, iterations);
	finalizeAllLogs();

	ProfileDealloc();


}
