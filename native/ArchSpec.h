#include <stdint.h>
#include <unistd.h>
#ifndef _ARCH_SPEC_H
#define _ARCH_SPEC_H
#include "MSR.h"
#include "CpuModels.h"

#define CPUID				\
    __asm__ volatile ("cpuid"   	\
			: "=a" (eax),	\
			"=b" (ebx),	\
			"=c" (ecx),	\
			"=d" (edx)	\
			: "0" (eax), "2" (ecx))

typedef struct APIC_ID_t {
	uint64_t smt_id;
	uint64_t core_id;
	uint64_t pkg_id;
	uint64_t os_id;
} APIC_ID_t;

typedef struct cpuid_info_t {
	uint32_t eax;
	uint32_t ebx;
	uint32_t ecx;
	uint32_t edx;
} cpuid_info_t;

#define UNDEFINED_ARCHITECTURE 0 //
#define READ_FROM_DRAM 1 // Used in the switch case statements in CPUScler.c to identify if the cpu model reads from DRAM buffer
#define READ_FROM_GPU 2 // Used in the switch case statements in CPUScler.c to identify if the cpu model reads from the GPU buffer
#define READ_FROM_DRAM_AND_GPU 3

/* None of these are global variables any more
extern uint32_t eax, ebx, ecx, edx;
extern uint32_t cpu_model;

int read_time = 0;
uint64_t max_pkg = 0;
uint64_t num_core_thread = 0; //number of physical threads per core
uint64_t num_pkg_thread = 0; //number of physical threads per package
uint64_t num_pkg_core = 0; //number of cores per package
uint64_t num_pkg = 0; //number of packages for current machine


extern int core;

extern int read_time;
extern uint64_t max_pkg;
extern uint64_t num_core_thread; //number of physical threads per core
extern uint64_t num_pkg_thread; //number of physical threads per package
extern uint64_t num_pkg_core; //number of cores per package
extern uint64_t num_pkg; //number of packages for current machine

extern int coreNum;
*/

uint32_t
get_cpu_model(void);

void
parse_apic_id(cpuid_info_t info_l0, cpuid_info_t info_l1, APIC_ID_t *my_id);

void
cpuid(uint32_t eax_in, uint32_t ecx_in, cpuid_info_t *ci);

cpuid_info_t
getProcessorTopology(uint32_t level);

uint64_t
get_num_core_thread();

uint64_t
get_num_pkg_thread();

uint64_t
get_num_pkg_core();

uint64_t
getSocketNum();

rapl_msr_unit
get_rapl_unit();

int 
get_power_domains_supported(uint32_t cpu_model, char power_domains_string_buffer[]);

#endif
