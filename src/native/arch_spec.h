#ifndef _ARCH_SPEC_H
#define _ARCH_SPEC_H

#include <stdint.h>
#include <unistd.h>
#include <stdbool.h>
#include "msr.h"
#include "platform_support.h"

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

#define CPUID \
  __asm__ volatile ("cpuid" : "=a" (eax), "=b" (ebx), "=c" (ecx), "=d" (edx) : "0" (eax), "2" (ecx))

uint32_t
get_micro_architecture(void);

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

power_domain_support_info_t
get_power_domains_supported();

bool
is_this_the_current_architecture(const char* candidate_arch_name);

void
get_arch_name(char buf[]);

#endif
