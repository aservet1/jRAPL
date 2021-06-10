#include <iostream>
#include <bitset>
#include <cassert>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <vector>

#include "monitor.h"

#define MSR_RAPL_POWER_UNIT      0x606
#define MSR_PP0_ENERGY_STATUS    0x639 //core
#define MSR_PP1_ENERGY_STATUS    0x641 //gpu
#define MSR_PKG_ENERGY_STATUS    0x611
#define MSR_DRAM_ENERGY_STATUS   0x619

#define _2pow(n) \
	1 << (n-1)

static uint64_t EnergyStatus::extractBits(uint64_t data, int lo, int hi) {
	return (data & (~0 << lo) & ~( ~0 << hi+1 )) >> lo;
}

static uint64_t	EnerguStatus::readMSR(int which) {
	uint64_t data = -1;
	if (pread(EnergyStatus::fd, &data, sizeof data, which) != sizeof data) {
		std::cerr << "pread error: " << strerror(errno) << std::endl;
	}
	return data;
}
EnergyStatus::EnergyStatus(double d, double g, double c, double p) {
	dram = d; gpu = g; core = c; pkg = p;
	for (auto p : {&dram, &gpu, &core, &pkg})
		if (p < 0) p += EnergyStatus::wraparound;
}

static void EnergyStatus::profileInit() { // static background initializer
	fd = open("/dev/cpu/0/msr",O_RDONLY);
	energyUnit = extractBits(
						readMSR(MSR_RAPL_POWER_UNIT),
						8, 12
				);
	wraparound = 0;
}

static void EnergyStatus::profileDealloc() {
	close(fd);
	energyUnit = wraparound = fd = -1;
}

double EnergyStatus::getDram() { return dram; }
double EnergyStatus::getCore() { return core; }
double EnergyStatus::getPkg() { return pkg; }
double EnergyStatus::getGpu() { return gpu; }

EnergyStatus::EnergyStatus() {
	dram = readMSR(MSR_DRAM_ENERGY_STATUS) * 1/_2pow(EnergyStatus::energyUnit); // this might not be accurate
	gpu = readMSR(MSR_PP1_ENERGY_STATUS) * 1/_2pow(EnergyStatus::energyUnit);
	core = readMSR(MSR_PP0_ENERGY_STATUS) * 1/_2pow(EnergyStatus::energyUnit);
	pkg = readMSR(MSR_PKG_ENERGY_STATUS) * 1/_2pow(EnergyStatus::energyUnit);
}
static EnergyStatus // eventually just make a - operator overload
EnergyStatus::between(EnergyStatus before, EnergyStatus after) {
	return EnergyStatus::EnergyStatus(-1, -1, -1, -1);
}


std::ostream& operator<<(std::ostream& out, const EnergyStatus& s) {
	out << std::hex
		<< "dram: " << s.dram << "," << "gpu: " << s.gpu << ","
		<< "pkg: " << s.pkg << "," << "core: " << s.core;
	return out;
}
