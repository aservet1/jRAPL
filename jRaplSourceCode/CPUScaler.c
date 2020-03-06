#include <stdio.h>
#include <jni.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
#include <stdint.h>
#include <string.h>
#include <inttypes.h>
#include "CPUScaler.h"
#include "arch_spec.h"
#include "msr.h"
#define UNDEFINED_ARCH 0
#define DRAM 1
#define GPU 2
static rapl_msr_parameter *parameters;
static int *fd;
static uint64_t num_pkg;


int get_arch_category(uint32_t cpu_model){
	if(
			cpu_model == SANDYBRIDGE_EP ||
			cpu_model == HASWELL1 ||
			cpu_model == HASWELL2 ||
			cpu_model == HASWELL3 ||
			cpu_model == HASWELL_EP ||
			cpu_model == SKYLAKE1 ||
			cpu_model == SKYLAKE2 ||
			cpu_model == BROADWELL ||
			cpu_model == BROADWELL2 ||
			cpu_model == APOLLOLAKE ||
			cpu_model == COFFEELAKE2
	) return DRAM;

	if(
		cpu_model ==  SANDYBRIDGE ||
		cpu_model == IVYBRIDGE
	) return GPU;

	return UNDEFINED_ARCH;
}

/// Comments starting with '///' are my (Alejandro's) notes to self. feel free to delete them if I haven't and they aren't useful to you. Same goes for the
/// <Alejandro's Intepretation> comments that describe functions. None of this is official documentation.

/** <Alejandro's Interpretation>
 *  Takes the energy info and packages it into a formatted string... # delimits the 3 energy attribs and @ delimits multiple package readings (1 pkg = 3 energy attribs)
 *	Sets offset to the end of the string by the end of this
 *	I belive the i is an iterator to see how many packages have been put into the string
 *	If more than 1 pkg, puts a @ at the end of the string because there's going to be another set of package info after that
 *
 */
void copy_to_string(char *ener_info, char uncore_buffer[60], int uncore_num, char cpu_buffer[60], int cpu_num, char package_buffer[60], int package_num, int i, int *offset) {
	memcpy(ener_info + *offset, &uncore_buffer, uncore_num);
	//split sign
	ener_info[*offset + uncore_num] = '#';
	memcpy(ener_info + *offset + uncore_num + 1, &cpu_buffer, cpu_num);
	ener_info[*offset + uncore_num + cpu_num + 1] = '#';
	if(i < num_pkg - 1) {
		memcpy(ener_info + *offset + uncore_num + cpu_num + 2, &package_buffer, package_num);
		*offset += uncore_num + cpu_num + package_num + 2;
		if(num_pkg > 1) {
			ener_info[*offset++] = '@';
//			*offset++;
		}
	} else {
		memcpy(ener_info + *offset + uncore_num + cpu_num + 2, &package_buffer, package_num + 1);
	}

}


/*Assumed to be called only during or after ProfileInit, when static variable fd[] is initialized
  and fd[0] actually means something*/
rapl_msr_unit get_rapl_unit()
{
	rapl_msr_unit rapl_unit;
	uint64_t unit_info = read_msr(fd[0], MSR_RAPL_POWER_UNIT);
	get_msr_unit(&rapl_unit, unit_info);
	return rapl_unit;
}

/** <Alejandro's Interpretation>
 *	Sets up an an energy profile. (*?)What exactly is an energy profile? A bunch of data stored about the current energy state...
 *	reads and stores CPU model, socketnum. calculates wraparound energy.
 *  the 'fd' array is an array of which msr regs. num msr regs is number of packages the computer has
 *  initializes the rapl unit (stuff holding the conversions to translate msr data sections into meaningful 'human-readable' stuff)
 */
JNIEXPORT jint JNICALL Java_jrapl_EnergyCheckUtils_ProfileInit(JNIEnv *env, jclass jcls) {
	int i;
	char msr_filename[BUFSIZ];
	int core = 0;
	rapl_msr_unit rapl_unit;

	num_pkg = getSocketNum();
	uint64_t num_pkg_thread = get_num_pkg_thread();

	jint wraparound_energy;

	/*only two domains are supported for parameters check*/
	parameters = (rapl_msr_parameter *)malloc(2 * sizeof(rapl_msr_parameter));
	fd = (int *) malloc(num_pkg * sizeof(int));

	for(i = 0; i < num_pkg; i++) {
		if(i > 0) {
			core += num_pkg_thread / 2; 	//measure the first core of each package
		}
		sprintf(msr_filename, "/dev/cpu/%d/msr", core);
		fd[i] = open(msr_filename, O_RDWR);
	}

	rapl_unit = get_rapl_unit();
	wraparound_energy = get_wraparound_energy(rapl_unit.energy);

	return wraparound_energy;
}



/** <Alejandro's Interpretation>
 * Gets num of cpu sockets but casts it as a jint for the java end of things
 */
JNIEXPORT jint JNICALL Java_jrapl_EnergyCheckUtils_GetSocketNum(JNIEnv *env, jclass jcls) {
	return (jint)getSocketNum();    ///<link> arch_spec.c
}

#define MSR_DRAM_ENERGY_UNIT 0.000015

/** <Alejandro's Interpretation>
 *  In short, fills up the energy info buffers appropriately.
 *	Pass in gpu, dram, cpu, and package buffers. There are num_pkg buffers per type of computer thing. One buffer per package that the computer has.
 *	Filling up the buffers. The for loop is so you get a different reading for all the packages. Reads the msr for that package with fd[i]
 *  Based on the CPU model, it either adds dram info or gpu info. I guess that certain models use gpus and others drams?
 *  Interpret/process MSR reading for dram differently based on CPU model before storing it in the buffer.......................
 */
void
initialize_energy_info(char gpu_buffer[num_pkg][60], char dram_buffer[num_pkg][60], char cpu_buffer[num_pkg][60], char package_buffer[num_pkg][60]) {

	uint32_t cpu_model = get_cpu_model();
	double package[num_pkg];
	double pp0[num_pkg];
	double pp1[num_pkg];
	double dram[num_pkg];
	double result = 0.0;
	int info_size = 0;
	int i = 0;
	rapl_msr_unit rapl_unit = get_rapl_unit();
	for (; i < num_pkg; i++) {

		result = read_msr(fd[i], MSR_PKG_ENERGY_STATUS);	//First 32 bits so don't need shift bits.
		package[i] = (double) result * rapl_unit.energy;

		result = read_msr(fd[i], MSR_PP0_ENERGY_STATUS);
		pp0[i] = (double) result * rapl_unit.energy;

		sprintf(package_buffer[i], "%f", package[i]);
		sprintf(cpu_buffer[i], "%f", pp0[i]);
		int arch_category = get_arch_category(cpu_model);
		switch(arch_category) {
			case DRAM:
				result = read_msr(fd[i],MSR_DRAM_ENERGY_STATUS);
				if (cpu_model == BROADWELL || cpu_model == BROADWELL2) {
					dram[i] =(double)result*MSR_DRAM_ENERGY_UNIT;
				} else {
					dram[i] =(double)result*rapl_unit.energy;
				}

				sprintf(dram_buffer[i], "%f", dram[i]);

				info_size += strlen(package_buffer[i]) + strlen(dram_buffer[i]) + strlen(cpu_buffer[i]) + 4;

				/*Insert socket number*/

				break;
			case GPU:
				result = read_msr(fd[i],MSR_PP1_ENERGY_STATUS);
				pp1[i] = (double) result *rapl_unit.energy;

				sprintf(gpu_buffer[i], "%f", pp1[i]);

				info_size += strlen(package_buffer[i]) + strlen(gpu_buffer[i]) + strlen(cpu_buffer[i]) + 4;
				break;
			case UNDEFINED_ARCH:
				printf("Architecture not found\n");
				break;

		}
	}
}


/** <Alejandro's Interpretation>
 * Makes a string from the energy info. Initializes energy info with that function above and
 *
 * The first entry is: Dram/uncore gpu energy (depends on the cpu architecture)
 * The second entry is: CPU energy
 * The third entry is: Package energy
 */
JNIEXPORT jstring JNICALL Java_jrapl_EnergyCheckUtils_EnergyStatCheck(JNIEnv *env,
		jclass jcls) {
	jstring ener_string;
	char gpu_buffer[num_pkg][60];
	char dram_buffer[num_pkg][60];
	char cpu_buffer[num_pkg][60];
	char package_buffer[num_pkg][60];
	int dram_num = 0L;	///  dram_num is the id number of that component
	int cpu_num = 0L;	///  same applies to the other x_num varaibles (num is id number)
	uint32_t cpu_model = get_cpu_model();

	int package_num = 0L;
	int gpu_num = 0L;
	//construct a string
	char ener_info[512];
	int i;
	int offset = 0;


  	bzero(ener_info, 512);
	initialize_energy_info(gpu_buffer, dram_buffer, cpu_buffer, package_buffer);
	int arch_catergory = get_arch_category(cpu_model);
	for(i = 0; i < num_pkg; i++) {
		switch(arch_catergory) {
			case DRAM:
				//copy_to_string(ener_info, dram_buffer, dram_num, cpu_buffer, cpu_num, package_buffer, package_num, i, &offset);
				/*Insert socket number*/
				dram_num = strlen(dram_buffer[i]);
				cpu_num = strlen(cpu_buffer[i]);
				package_num = strlen(package_buffer[i]);

				memcpy(ener_info + offset, &dram_buffer[i], dram_num);
				//split sign
				ener_info[offset + dram_num] = '#';
				memcpy(ener_info + offset + dram_num + 1, &cpu_buffer[i], cpu_num);
				ener_info[offset + dram_num + cpu_num + 1] = '#';
				if(i < num_pkg - 1) {
					memcpy(ener_info + offset + dram_num + cpu_num + 2, &package_buffer[i], package_num);
					offset += dram_num + cpu_num + package_num + 2;
					if(num_pkg > 1) {
						ener_info[offset] = '@';
						offset++;
					}
				} else {
					memcpy(ener_info + offset + dram_num + cpu_num + 2, &package_buffer[i], package_num + 1);
				}

				break;
			case GPU:

				gpu_num = strlen(gpu_buffer[i]);
				cpu_num = strlen(cpu_buffer[i]);
				package_num = strlen(package_buffer[i]);

				//copy_to_string(ener_info, gpu_buffer, gpu_num, cpu_buffer, cpu_num, package_buffer, package_num, i, &offset);
				memcpy(ener_info + offset, &gpu_buffer[i], gpu_num);
				//split sign
				ener_info[offset + gpu_num] = '#';
				memcpy(ener_info + offset + gpu_num + 1, &cpu_buffer[i], cpu_num);
				ener_info[offset + gpu_num + cpu_num + 1] = '#';
				if(i < num_pkg - 1) {
					memcpy(ener_info + offset + gpu_num + cpu_num + 2, &package_buffer[i], package_num);
					offset += gpu_num + cpu_num + package_num + 2;
					if(num_pkg > 1) {
						ener_info[offset] = '@';
						offset++;
					}
				} else {
					memcpy(ener_info + offset + gpu_num + cpu_num + 2, &package_buffer[i],
							package_num + 1);
				}

				break;
		case UNDEFINED_ARCH:
				printf("Architecture not found\n");
				break;

		}
	}

	//// hmm why would be turn it into a string just to turn it back into an array in java's getEnergyStats()?
	ener_string = (*env)->NewStringUTF(env, ener_info);
	return ener_string;

}

/** <Alejandro's Interpretation>
 * Free memory allocated by profile init function
 */
JNIEXPORT void JNICALL Java_jrapl_EnergyCheckUtils_ProfileDealloc
   (JNIEnv * env, jclass jcls) {
	free(fd);
	free(parameters);
}
