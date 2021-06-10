#include <stdio.h>
#include <stdint.h>

/** Prints the hex id for your micro_architecture. Exctracted from jRAPL's ArchSpec code */

#define CPUID				\
    __asm__ volatile ("cpuid"   	\
			: "=a" (eax),	\
			"=b" (ebx),	\
			"=c" (ecx),	\
			"=d" (edx)	\
			: "0" (eax), "2" (ecx))
uint32_t
get_micro_architecture(void)
{
	uint32_t eax, ebx, ecx, edx;
    eax = 0x01;
	ecx = 0x02; //might be the wrong value to put here, but had to put something to not make compiler warnings when ecx used in CPUID
	CPUID;
	return (((eax>>16)&0xFU)<<4) + ((eax>>4)&0xFU);
}


int main() {
	printf("micro_architecture: %x\n",get_micro_architecture());
}
