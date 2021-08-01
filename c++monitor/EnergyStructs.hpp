#include <iostream>
#include "rapl.h"
#include "mysleep.h"


struct EnergyStats {
	double dram, core, gpu, pkg;
	unsigned long timestamp;
	double* fields = {
		&dram, &pkg,
		&core, &gpu,
	};
};

struct EnergyStamp : EnergyStats {
	EnergyStamp();
};

struct EnergySample : EnergyStats {
	private:
		void findDifferences(EnergyStamp& start, EnergyStamp& end);
	public:
		EnergySample(int msec);
		EnergySample(EnergyStamp& start, EnergyStamp& end);
};
