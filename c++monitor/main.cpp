#include <iostream>
#include "rapl.h"
#include "mysleep.h"

#define toUsec(timeval) ( timeval.tv_sec * 1'000'000 + timeval.tv_usec )

struct EnergyStats {
	double dram;
	double core;
	double gpu;
	double pkg;
	struct timeval timestamp;
	EnergyStats() {
		gettimeofday(&timestamp,nullptr);
	}
	int usec_timestamp() {
		return toUsec(timestamp);
	}
};

struct EnergyStamp : EnergyStats {
	EnergyStamp() {
		dram = rapl::DRAM();
		core = rapl::CORE();
		gpu = rapl::GPU();
		pkg = rapl::PKG();
	}
};

struct EnergySample : EnergyStats {
	private:
		int wrapAround_TEMP = 16384;
		double* field_iter = {&dram, &core, &pkg, &gpu};
		struct timeval elapsed;
		findDifferences(EnergyStamp& start, EnergyStamp& end) {
			dram = end.dram - start.dram;
			core = end.core - start.core;
			gpu  =  end.gpu - start.gpu;;
			pkg  =  end.pkg - start.pkg;;
			for (double* field_ptr : field_iter)
				if (*field_ptr < 0)
					*field_ptr += wrapAround_TEMP;
			timersub(
				end.timestamp,
				start.timestamp,
				elapsed
			);
		}
	public:
		EnergySample(int msec) {
			EnergyStamp start;
			sleep_msec(msec);
			EnergyStamp end;
			findDifferences( start, end );
		}
		EnergySample::between(EnergyStamp& start, EnergyStamp& end) {
			findDifferences(start, end);
		}
		int usec_elapsed() {
			return toUsec(elapsed);
		}
};

int main(int argc, char *argv[])
{
	rapl::Init();

	EnergyStamp e;

	std::cout << e.dram << " ~ " << e.core+1 << "~"  << e.gpu << "~" << e.pkg << std::endl;
	printf("%f~%f~%f~%f\n", e.dram, e.core, e.gpu, e.pkg);

	rapl::Dealloc();
	return 0;
}
