#include <stdio.h>
#include <jni.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
#include <stdint.h>
#include <string.h>
#include "arch_spec.h"

/** <Alejandro's Interpretation>
 *	- (?) Direct  CPUID  access  through  this  device
          should only be used in exceptional cases.
 *	Calls cpuid with eax=1 which returns info abt if its SANDYBRIDGE, BROADWELL, ...
 *	EAX	Version Information: Type, Family, Model, and Stepping ID
-	EBX	Bits 7-0: Brand Index
-	-	Bits 15-8: CLFLUSH line size (Value . 8 = cache line size in bytes)
-	-	Bits 23-16: Number of logical processors per physical processor; two for the Pentium 4 processor supporting Hyper-Threading Technology
 */
uint32_t
get_cpu_model(void)
{
	uint32_t eax, ebx, ecx, edx;
    eax = 0x01;
	ecx = 0x02; //might be the wrong value to put here, but had to put something to not make compiler warnings when ecx used in CPUID
	CPUID;
	return (((eax>>16)&0xFU)<<4) + ((eax>>4)&0xFU);
}

/** <Alejandro's Interpretation>
 * Gets the number of processors with sysconf. # processors == # cores (right(?))
 */
int core_num() {
	return sysconf(_SC_NPROCESSORS_CONF); //passed in is number of configured processors
}

/** <Alejandro's Interpretation>
 *	Gets info from cpuid call to identify where the APIC stuff is.
 *	To my understnading, APIC stuff can target parts of the process and interrupt it // APIC the advanced programmable interrupt controller
-	tech that intel developed to streamline interrupt handling on multiprocessor systems

 *	We care about this bc we want to interrupt processes that pass a certain energy level
 */
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

/** <Alejandro's Interpretation>
 *	Gets CPUID info given eax_in and ecx_in and eax and ecx x86 args. Stores
 *  reulting e[a/b/c/d]x values in a cpuid_info_t struct
 */
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


/** <Alejandro's Interpretation>
 *  Wraps up the cpuid function that gets cpuid information stuff. More abstract and easy to deal with
 *	no specific numbers or assembly or whatever. Always passes in 0xb because thats the cpuid() arg that
 *	gives info about packages and cores and APIC info
 *
 *	see intel manual pdf p. 771 for info about when eax_in = 0x0b; ebx bits 15-00 are number of logical preprocessors at this level type
 *		the number reflects configuration as shipped by Intel

	- INTEL: CPUID eax=0x0000000B
	  For Intel CPUs (and not AMD), this CPUID function tells you "Number of bits to shift right APIC ID to get next level APIC ID", and needs to be used twice. The 		  first time (with ECX=0) it tells you how many bits of the APIC ID is used to identify the logical CPU within each core (logical_CPU_bits). The second time (with
	  ECX=1) it tells you how many bits of the APIC ID is used to identify the core and logical CPU within the chip, and to get "core_bits" from this value you subtract
	  "logical_CPU_bits" from it.
 */
cpuid_info_t getProcessorTopology(uint32_t level) {
	cpuid_info_t info;
	cpuid(0xb, level, &info); ///define a constant for 0xb at some point...
	return info;
}

uint64_t get_num_core_thread()
{
	uint32_t level1 = 0;
	cpuid_info_t infol0 = getProcessorTopology(level1);
	return infol0.ebx & 0xffff;
}

uint64_t get_num_pkg_thread()
{
	uint32_t level2 = 1;
	cpuid_info_t infol1 = getProcessorTopology(level2);
	return infol1.ebx & 0xffff;
}

uint64_t get_num_pkg_core()
{
		uint32_t num_core_thread = get_num_core_thread();
		uint32_t num_pkg_thread = get_num_pkg_thread();
		return num_pkg_thread / num_core_thread;
}

/** <Alejandro's Interpretation>
 *	Initializes some data about the system, returns number of cores.

	Below used to be global variables (hey i made these not global any more, should
    probably update the comments for all the functions at some point...)
    
	  num_core_thread; 	//number of physical threads per core
	  num_pkg_thread; 	//number of physical threads per package
	  num_pkg_core;		//number of cores per package
	  num_pkg; 			//number of packages for current machine

 */
uint64_t getSocketNum() {

	int coreNum = core_num();
	uint64_t num_pkg_thread = get_num_pkg_thread();
	uint64_t num_pkg = coreNum / num_pkg_thread;

	return num_pkg;
}

void multiply_string_by_socket_num(char out_buffer[], char string[]) {

	int socketnum = getSocketNum();
	int string_len = strlen(string);
	int offset = 0;
	for (int i = 0; i < socketnum; i++) {
		memcpy(out_buffer + offset, string, string_len);
		offset += string_len;
	}
	out_buffer[++offset] = '\0';

}

int get_power_domains_supported(uint32_t cpu_model, char power_domain_string_buffer[512]) {

	char* string;
		
	switch (cpu_model) {

		case KABYLAKE:
		case BROADWELL:

			string = "dram,gpu,core,pkg@";
			if (power_domain_string_buffer != NULL) {
				bzero(power_domain_string_buffer, 512);
				multiply_string_by_socket_num(power_domain_string_buffer,string);
			}
			return READ_FROM_DRAM_AND_GPU;

		case SANDYBRIDGE_EP:			case HASWELL1:		case HASWELL2:
		case HASWELL3:				case HASWELL_EP:	case SKYLAKE1:
		case SKYLAKE2: 				case BROADWELL2:
		case APOLLOLAKE:			case COFFEELAKE2:

			string = "dram,core,pkg@";
			if (power_domain_string_buffer != NULL) {
				bzero(power_domain_string_buffer, 512);
				multiply_string_by_socket_num(power_domain_string_buffer,string);
			}
			return READ_FROM_DRAM;

		case SANDYBRIDGE:
		case IVYBRIDGE:

			string = "gpu,core,pkg@";
			if (power_domain_string_buffer != NULL) {
				bzero(power_domain_string_buffer, 512);
				multiply_string_by_socket_num(power_domain_string_buffer,string);
			}
			return READ_FROM_GPU;

		default:

			if (power_domain_string_buffer != NULL) {
				bzero(power_domain_string_buffer, 512);
				sprintf(power_domain_string_buffer,"undefined_architecture");
			}

			return UNDEFINED_ARCHITECTURE;
	}

}

JNIEXPORT jstring JNICALL
Java_jRAPL_ArchSpec_energyStatsStringFormat(JNIEnv* env, jclass jcls) {
	char power_domain_string[512];
	get_power_domains_supported(get_cpu_model(),power_domain_string);
	return (*env)->NewStringUTF(env, power_domain_string);
	
}

//TODO -- for organization, see if you can do the wraparound energy calculation here
//	instead of CPUScaler. involves open()-ing up the msr and closing it (if not already open)
//  and reading directly from it. that would make it so you don't have to do ProfileInit()
//  if you just want to read the wraparound energy real quick

JNIEXPORT jint JNICALL
Java_jRAPL_ArchSpec_powerDomainsSupported(JNIEnv * env, jclass jcls) {
	return get_power_domains_supported(get_cpu_model(),NULL);
}

JNIEXPORT jint JNICALL
Java_jRAPL_ArchSpec_getSocketNum(JNIEnv *env, jclass jcls) {
	return (jint)getSocketNum(); 
}

JNIEXPORT jint JNICALL
Java_jRAPL_ArchSpec_getCpuModel(JNIEnv* env, jclass jcls) {
	return get_cpu_model();
}

JNIEXPORT jstring JNICALL
Java_jRAPL_ArchSpec_getCpuModelName(JNIEnv* env, jclass jcls) {
	const char* name;
	switch(get_cpu_model()) {
		case KABYLAKE:			name = "KABYLAKE";			break;
		case BROADWELL:			name = "BROADWELL";			break;
		case SANDYBRIDGE_EP:	name = "SANDYBRIDGE_EP";	break;
		case HASWELL3:			name = "HASWELL3";			break;
		case SKYLAKE2:			name = "SKYLAKE2";			break;
		case APOLLOLAKE:		name = "APOLLOLAKE";		break;
		case SANDYBRIDGE:		name = "SANDYBRIDGE";		break;
		case IVYBRIDGE:			name = "IVYBRIDGE";			break;
		case HASWELL1:			name = "HASWELL1";			break;		
		case HASWELL_EP:		name = "HASWELL_EP";		break;	
		case COFFEELAKE2:		name = "COFFEELAKE2";		break;
		case BROADWELL2:		name = "BROADWELL2";		break;
		case HASWELL2:			name = "HASWELL2";			break;
		case SKYLAKE1:			name = "SKYLAKE1";			break;
		default: name = "UNDEFINED_ARCHITECTURE";
	}
	return (*env)->NewStringUTF(env, name);
}


