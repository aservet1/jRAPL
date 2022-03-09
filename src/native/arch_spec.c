#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
#include <stdint.h>
#include <string.h>
#include <stdbool.h>
#include "arch_spec.h"
#include "msr.h"
#include "platform_support.h"

/**
	Calls cpuid with eax=1 which returns info abt if its SANDYBRIDGE, BROADWELL, ...
	EAX	Version Information: Type, Family, Model, and Stepping ID
	EBX	Bits 7-0: Brand Index
		Bits 15-8: CLFLUSH line size (Value . 8 = cache line size in bytes)
		Bits 23-16: Number of logical processors per physical processor; two for the Pentium 4 processor supporting Hyper-Threading Technology
 */
uint32_t
get_micro_architecture(void)
{
	uint32_t eax, ebx, ecx, edx;
    eax = 0x01;
	ecx = 0x02; //might be the wrong value to put here, but had to put something to not make compiler warnings when ecx used in CPUID
	CPUID;
	return (((eax>>16)&0xFU)<<4) + ((eax>>4)&0xFU);
}

int
core_num() {
	return sysconf(_SC_NPROCESSORS_CONF);
}

void
parse_apic_id(cpuid_info_t info_l0, cpuid_info_t info_l1, APIC_ID_t *my_id){

	// Get the SMT ID (SMT = Simultaneous MultiTh/reading)
	uint64_t smt_mask_width = info_l0.eax & 0x1f;
	uint64_t smt_mask = ~((-1) << smt_mask_width);
	my_id->smt_id = info_l0.edx & smt_mask;

	// Get the core ID
	uint64_t core_mask_width = info_l1.eax & 0x1f;
	uint64_t core_mask = (~((-1) << core_mask_width ) ) ^ smt_mask;
	my_id->core_id = (info_l1.edx & core_mask) >> smt_mask_width;

	// Get the package ID
	uint64_t pkg_mask = (-1) << core_mask_width;
	my_id->pkg_id = (info_l1.edx & pkg_mask) >> core_mask_width;
}

void
cpuid(uint32_t eax_in, uint32_t ecx_in, cpuid_info_t *ci) {
	 asm (
#if defined(__LP64__)           /* 64-bit architecture */
	     "cpuid;"                /* execute the cpuid instruction */
	     "movl %%ebx, %[ebx];"   /* save ebx output */
#else                           /* 32-bit architecture */
	     "pushl %%ebx;"          /* save ebx */
	     "cpuid;"                /* execute the cpuid instruction */
	     "movl %%ebx, %[ebx];"   /* save ebx output */
	     "popl %%ebx;"           /* restore ebx */
#endif
             : "=a"(ci->eax), [ebx] "=r"(ci->ebx), "=c"(ci->ecx), "=d"(ci->edx)
             : "a"(eax_in), "c"(ecx_in)
        );
}

/** 
	See Intel Manual pdf for info about why eax_in = 0x0B; ebx bits 15-00 are number of
	logical preprocessors at this level type the number reflects configuration as
	shipped by Intel

	CPUID eax=0xB
	For Intel CPUs (and not AMD), this CPUID function tells you "Number of bits to shift right APIC ID to get next level APIC ID", and needs to be used twice. The 		  first time (with ECX=0) it tells you how many bits of the APIC ID is used to identify the logical CPU within each core (logical_CPU_bits). The second time (with
	ECX=1) it tells you how many bits of the APIC ID is used to identify the core and logical CPU within the chip, and to get "core_bits" from this value you subtract
	"logical_CPU_bits" from it.
 */
cpuid_info_t
getProcessorTopology(uint32_t level) {
	cpuid_info_t info;
	cpuid(0xb, level, &info);
	return info;
}

uint64_t
get_num_core_thread() {
	uint32_t level1 = 0;
	cpuid_info_t infol0 = getProcessorTopology(level1);
	return infol0.ebx & 0xffff;
}

uint64_t
get_num_pkg_thread() {
	uint32_t level2 = 1;
	cpuid_info_t infol1 = getProcessorTopology(level2);
	return infol1.ebx & 0xffff;
}

uint64_t
get_num_pkg_core() {
	uint32_t num_core_thread = get_num_core_thread();
	uint32_t num_pkg_thread = get_num_pkg_thread();
	return num_pkg_thread / num_core_thread;
}

uint64_t
getSocketNum() {

	int coreNum = core_num();
	uint64_t num_pkg_thread = get_num_pkg_thread();
	uint64_t num_pkg = coreNum / num_pkg_thread;

	return num_pkg; //@TODO: get rid of this num_pkg thing, num_sockets is clearer (to me)
}

bool is_platform_defined(uint32_t microarch_id) {
    for(int i = 0; i < NUM_PLATFORMS_SUPPORTED; ++i) {
		// printf("%x\n",PLATFORM_SUPPORT_TABLE[i].cpuid);
        if (KNOWN_PLATFORM_ID_SET[i] == microarch_id) return true;
    } return false;
}

power_domain_support_info_t
get_power_domains_supported() {
    uint32_t microarch_id = get_micro_architecture();
    if (is_platform_defined(microarch_id)) {
        return PLATFORM_SUPPORT_TABLE[microarch_id];
    }
	return PLATFORM_NOT_SUPPORTED;
}

bool is_this_the_current_architecture(const char* candidate_arch_name) {
	uint32_t myarch = get_micro_architecture();
	if (is_platform_defined(myarch)) {
		return ( 0 == strcmp(PLATFORM_SUPPORT_TABLE[myarch].name,candidate_arch_name) );
	}
	return false;
}

void get_arch_name(char buf[]) {
	uint32_t myarch = get_micro_architecture();
	if (is_platform_defined(myarch)) {
		sprintf(buf,"%s",PLATFORM_SUPPORT_TABLE[myarch].name);
	} else {
		sprintf(buf,"%s","UNDEFINED_ARCHITECTURE");
	}
}
