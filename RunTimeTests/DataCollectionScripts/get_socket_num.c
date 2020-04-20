#include <stdio.h>
#include <unistd.h>
#include <stdint.h>

typedef struct cpuid_info_t {
	uint32_t eax;
	uint32_t ebx;
	uint32_t ecx;
	uint32_t edx;
} cpuid_info_t;

void cpuid(uint32_t eax_in, uint32_t ecx_in, cpuid_info_t *ci) {
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

cpuid_info_t getProcessorTopology(uint32_t level)
{
	cpuid_info_t info;
	cpuid(0xb, level, &info);
	return info;
}

uint64_t get_num_pkg_thread()
{
	uint32_t level2 = 1;
	cpuid_info_t infol1 = getProcessorTopology(level2);
	return infol1.ebx & 0xffff;
}

uint64_t getSocketNum() {

	int coreNum = sysconf(_SC_NPROCESSORS_CONF);
	uint64_t num_pkg_thread = get_num_pkg_thread();
	uint64_t num_pkg = coreNum / num_pkg_thread;

	return num_pkg;
}

/* Prints how many sockets the current computer has */
int main()
{
	printf("%ld\n",getSocketNum());
}
