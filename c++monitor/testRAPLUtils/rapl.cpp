#include <iostream>
#include <bitset>
#include <cassert>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <vector>

#include "rapl.hh"

#define MSR_RAPL_POWER_UNIT      0x606
#define MSR_PP0_ENERGY_STATUS    0x639 // pp0 is core
#define MSR_PP1_ENERGY_STATUS    0x641 // pp1 is gpu
#define MSR_PKG_ENERGY_STATUS    0x611
#define MSR_DRAM_ENERGY_STATUS   0x619

#define _2pow(n) ( (n==0) ? 1 : 2 << (n-1) )  // 2**n

static int fd, energyUnit, wrapAround;
static double energyConversion;

static uint64_t
extractBitField(uint64_t data, int lo, int hi) {
	return (data & (~0 << lo) & ~( ~0 << hi+1 )) >> lo;
}

static uint64_t
readMSR(int whichMSR) {
	uint64_t data = -1;
	if (pread(fd, &data, sizeof data, whichMSR) != sizeof data) {
		std::cerr << "pread error: " << strerror(errno) << std::endl;
		data = -1;
	}
	return data;
}
// - *$ --------------------------------------------------------------------------------------- $%
void																							//
raplInit() { // static background initializer													//
	fd = open("/dev/cpu/0/msr",O_RDONLY);														//
	energyUnit = extractBitField (	readMSR(MSR_RAPL_POWER_UNIT),	8, 12	);					//
	energyConversion = 1.0 / (double)_2pow(energyUnit);											//
	wrapAround = 0;																				//
}																								//
																								//
void																							//
raplDealloc() {																					//
	close(fd);																					//
	energyUnit = wrapAround = fd = -1;															//
}																								//
																								//
double raplDRAM() { return readMSR(MSR_DRAM_ENERGY_STATUS) * energyConversion; }				//
double raplCORE() { return readMSR(MSR_PP0_ENERGY_STATUS)  * energyConversion; }				//
double raplPKG()  { return readMSR(MSR_PKG_ENERGY_STATUS)  * energyConversion; }				//
double raplGPU()  { return readMSR(MSR_PP1_ENERGY_STATUS)  * energyConversion; }				//

double raplSubtract(double a, double b) {
	std::cout << a << "," << b;
	if (a == -1 || b == -1) return -1;
	double d = a - b;
	if (d < 0) d += wrapAround;
	return d;
}


// - $* --------------------------------------------------------------------------------------- %$
