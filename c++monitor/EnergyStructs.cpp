#include <iostream>
#include <sys/time.h>
#include "rapl.h"
#include "mysleep.h"
#include "EnergyStructs.hpp"

#define toUsec(timeval) ( timeval.tv_sec * 1'000'000 + timeval.tv_usec )

EnergyStats::EnergyStats() {
	struct timeval _timestamp;
	gettimeofday(&_timestamp,nullptr);
	timestamp = toUsec(_timestamp);
}

EnergyStamp::EnergyStamp() {
		dram = rapl::DRAM();
		pkg = rapl::PKG();
		core = rapl::CORE();
		gpu = rapl::GPU();
	}
};

static void
EnergySample::findDifferences(EnergyStamp& start, EnergyStamp& end) {
	int wrapAround_TEMP = 16384;
	dram = end.dram - start.dram;
	core = end.core - start.core;
	gpu  =  end.gpu - start.gpu;;
	pkg  =  end.pkg - start.pkg;;
	for (double* field_ptr : fields)
		if (*field_ptr < 0)
			*field_ptr += wrapAround_TEMP;
	timersub(
		end.timestamp,
		start.timestamp,
		elapsed
	);
}

EnergySample::EnergySample(int msec) {
	EnergyStamp start;
	sleep_msec(msec);
	EnergyStamp end;
	findDifferences( start, end );
}

EnergySample::EnergySample(EnergyStamp& start, EnergyStamp& end) {
	findDifferences(start, end);
}
