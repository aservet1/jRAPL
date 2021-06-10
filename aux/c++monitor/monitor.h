#pragma once

#include <iostream>

#define MSR_RAPL_POWER_UNIT      0x606
#define MSR_PP0_ENERGY_STATUS    0x639 //core
#define MSR_PP1_ENERGY_STATUS    0x641 //gpu
#define MSR_PKG_ENERGY_STATUS    0x611
#define MSR_DRAM_ENERGY_STATUS   0x619

#define _2pow(n) \
	1 << (n-1)

class EnergyStatus {
	private:
		double dram, core, pkg, gpu;
		static int fd, energyUnit, wraparound;
		
		static uint64_t readMSR(int which); 
		static uint64_t extractBits(uint64_t data, int lo, int hi); 
	public:
		EnergyStatus(); 
		EnergyStatus(double d, double g, double c, double p);

		double getDram(); 
		double getCore(); 
		double getPkg(); 
		double getGpu(); 

		static void profileInit(); 
		static void profileDealloc(); 
		static EnergyStatus between(EnergyStatus before, EnergyStatus after); 

		friend std::ostream& operator<<(std::ostream& out, const EnergyStatus& s);
};

