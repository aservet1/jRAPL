#include <iostream>
#include <unistd.h>
#include "rapl.hh"

int main(int argc, char *argv[])
{
	raplInit();
	
	double before = raplDRAM();
	sleep(1);
	double after = raplDRAM();

	std::cout << "DRAM over 1 sec: " << raplSubtract(after, before) << std::endl;

	raplDealloc();
	return 0;
}
