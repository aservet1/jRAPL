#include <iostream>
#include <bitset>
#include <cassert>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <vector>

#include "RAPLutils.h"

#define MSR_RAPL_POWER_UNIT      0x606
#define MSR_PP0_ENERGY_STATUS    0x639 //core
#define MSR_PP1_ENERGY_STATUS    0x641 //gpu
#define MSR_PKG_ENERGY_STATUS    0x611
#define MSR_DRAM_ENERGY_STATUS   0x619

#define _2tothepowerof(n) \
	1 << (n-1)

static int fd, energyUnit, wrapAround;

static uint64_t
extractBits(uint64_t data, int lo, int hi) {
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
// - $$ --------------------------------------------------------------------------------------- $$
void																							//
rapl::Init() { // static background initializer													//
	fd = open("/dev/cpu/0/msr",O_RDONLY);														//
	energyUnit = extractBits (	readMSR(MSR_RAPL_POWER_UNIT),									//
								8, 12	);														//
	wrapAround = 0;																				//
}																								//
																								//
void																							//
rapl::Dealloc() {																				//
	close(fd);																					//
	energyUnit = wrapAround = fd = -1;															//
}																								//
																								//
double rapl::DRAM() { return readMSR(MSR_DRAM_ENERGY_STATUS) * 1/_2tothepowerof(energyUnit);	//
double rapl::CORE() { return readMSR(MSR_PP0_ENERGY_STATUS)  * 1/_2tothepowerof(energyUnit);	//
double rapl::PKG()  { return readMSR(MSR_PKG_ENERGY_STATUS)  * 1/_2tothepowerof(energyUnit);	//
double rapl::GPU()  { return readMSR(MSR_PP1_ENERGY_STATUS)  * 1/_2tothepowerof(energyUnit);	//
// - $$ ------------------------------------------------------------------------------------	$$
