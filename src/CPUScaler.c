#include <stdio.h>
#include <jni.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
#include <stdint.h>
#include <string.h>
#include <inttypes.h>
#include <sys/types.h>

#include "CPUScaler.h"
#include "arch_spec.h"
#include "msr.h"
#include "EnergyStats.h"

#define MSR_DRAM_ENERGY_UNIT 0.000015

static int architecture_category; //TODO - get better name for this variable
static uint32_t cpu_model;
static rapl_msr_unit rapl_unit;
static rapl_msr_parameter *parameters;
static int *fd;
static uint64_t num_pkg;
static int wraparound_energy = -1;

rapl_msr_unit get_rapl_unit()
{
	rapl_msr_unit rapl_unit;
	uint64_t unit_info = read_msr(fd[0], MSR_RAPL_POWER_UNIT);
	get_msr_unit(&rapl_unit, unit_info);
	return rapl_unit;
}


int ProfileInit()
{
	int i;
	char msr_filename[BUFSIZ];
	int core = 0;
//	int wraparound_energy;

	num_pkg = getSocketNum();
	cpu_model = get_cpu_model();
	architecture_category = get_architecture_category(cpu_model);
	uint64_t num_pkg_thread = get_num_pkg_thread();

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
 *	Sets up an an energy profile.
 *	reads and stores CPU model, socketnum. calculates wraparound energy.
 *  the 'fd' array is an array of which msr regs. num msr regs is number of packages the computer has
 *  initializes the rapl unit (stuff holding the conversions to translate msr data sections into meaningful 'human-readable' stuff)
 */
JNIEXPORT jint JNICALL Java_jrapl_JRAPL_ProfileInit(JNIEnv *env, jclass jcls)
{	
	int wraparound_energy = ProfileInit();
	return wraparound_energy;
}

/** <Alejandro's Interpretation>
 *	Return number of CPU sockets
 */
JNIEXPORT jint JNICALL Java_jrapl_ArchSpec_GetSocketNum(JNIEnv *env, jclass jcls) {
	return (jint)getSocketNum(); 
}

JNIEXPORT jint JNICALL Java_jrapl_ArchSpec_DramOrGpu(JNIEnv * env, jclass jcls) {
	return get_architecture_category(get_cpu_model());
}

static inline double read_Package(int socket)
{
	double result = read_msr(fd[socket], MSR_PKG_ENERGY_STATUS);	//First 32 bits so don't need shift bits.
	return (double) result * rapl_unit.energy;
}
static inline double read_Cpu(int socket)
{
	double result = read_msr(fd[socket], MSR_PP0_ENERGY_STATUS);
	return (double) result * rapl_unit.energy;
}
static inline double read_Gpu(int socket)
{
	double result = read_msr(fd[socket],MSR_PP1_ENERGY_STATUS);
	return (double) result * rapl_unit.energy;
}
static inline double read_Dram(int socket)
{
	double result = read_msr(fd[socket],MSR_DRAM_ENERGY_STATUS);
	if (cpu_model == BROADWELL || cpu_model == BROADWELL2) {
		return (double) result * MSR_DRAM_ENERGY_UNIT;
	} else {
		return (double) result * rapl_unit.energy;
	}
}


/** <Alejandro's Interpretation>
 *	Reads energy info from MSRs into EnergyStats structs. Fills an array of structs, one per socket 
 */
void EnergyStatCheck(EnergyStats stats_per_socket[num_pkg])
{
	struct timeval timestamp;
	double pkg[num_pkg];
	double pp0[num_pkg]; //cpu
	double pp1[num_pkg]; //gpu
	double dram[num_pkg];

	for (int i = 0; i < num_pkg; i++)
	{
		pkg[i] = read_Package(i);
		pp0[i] = read_Cpu(i);

		switch(architecture_category) {
			case READ_FROM_DRAM_AND_GPU:
				dram[i] = read_Dram(i);
				pp1[i] = read_Gpu(i);
				break;

			case READ_FROM_DRAM:
				dram[i] = read_Dram(i);
				pp1[i] = -1;
				break;

			case READ_FROM_GPU:
				dram[i] = -1;
				pp1[i] = read_Gpu(i);
				break;

			case UNDEFINED_ARCHITECTURE:
				printf("Architecture not found\n");
				break;

		}

		stats_per_socket[i].socket = i + 1;
		stats_per_socket[i].pkg = pkg[i];
		stats_per_socket[i].cpu = pp0[i];
		stats_per_socket[i].gpu = pp1[i];
		stats_per_socket[i].dram = dram[i];
		gettimeofday(&timestamp,NULL);
		stats_per_socket[i].timestamp = timestamp;
	}

}

/** <Alejandro's Interpretation>
 *  Takes the energy info and packages it into a formatted string to pass to Java
 *    dram#gpu#cpu#pkg@
 *  Each socket will have the above format, multi socket machines will look like
 *    socket1@socket2@socket3@ etc
 *  Excludes the timestamp because Java will do its own timestamp upon receiving this information
 */
static void copy_to_string(EnergyStats stats_per_socket[num_pkg], char ener_info[512])
{
  	bzero(ener_info, 512);
	int offset = 0;

	char buffer[100];
	int buffer_len;

	for(int i = 0; i < num_pkg; i++){
		EnergyStats stats = stats_per_socket[i];
		bzero(buffer, 100);
		sprintf(buffer, "%f#%f#%f#%f@", stats.dram, stats.gpu, stats.cpu, stats.pkg);
		buffer_len = strlen(buffer);
		memcpy(ener_info + offset, buffer, buffer_len);
		offset += buffer_len;
	}
}

/** <Alejandro's Interpretation>
 * Read EnergyStats into EnergyStats struct (one struct per socket) and convert the structs
 * you have into a string to pass up to Java
 */
JNIEXPORT jstring JNICALL Java_jrapl_EnergyCheckUtils_EnergyStatCheck(JNIEnv *env, jclass jcls) {
	
	char ener_info[512];
	EnergyStats stats_per_socket[num_pkg];

	EnergyStatCheck(stats_per_socket);
	copy_to_string(stats_per_socket, ener_info);

	jstring ener_string = (*env)->NewStringUTF(env, ener_info);
  	
	return ener_string;

}

void ProfileDealloc()
{
	free(fd);
	free(parameters);
}

/** <Alejandro's Interpretation>
 * Free memory allocated by profile init function
 */
JNIEXPORT void JNICALL Java_jrapl_JRAPL_ProfileDealloc(JNIEnv * env, jclass jcls) {

	ProfileDealloc();

}
