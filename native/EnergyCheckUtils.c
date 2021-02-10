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

#include "EnergyCheckUtils.h"
#include "ArchSpec.h"
#include "MSR.h"
#include "EnergyStats.h"

#define MSR_DRAM_ENERGY_UNIT 0.000015

static int power_domains_supported;
static uint32_t cpu_model;
static rapl_msr_unit rapl_unit;
static rapl_msr_parameter *parameters;
static int *fd;
static uint64_t num_pkg;
static int wraparound_energy = -1;

int rapl_unit_fd() {
	return fd[0];
}

// only valid 
int* get_msr_fds() {
	return fd;
}

static inline double read_Pkg(int socket)
{
	double result = read_msr(fd[socket], MSR_PKG_ENERGY_STATUS);	//First 32 bits so don't need shift bits.
	return (double) result * rapl_unit.energy;
}
static inline double read_Core(int socket)
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

void ProfileInit()
{
	int i;
	char msr_filename[BUFSIZ];
	int core = 0;

	num_pkg = getSocketNum(); 
	cpu_model = get_cpu_model();
	power_domains_supported = get_power_domains_supported(cpu_model,NULL);
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

	rapl_unit = get_rapl_unit(fd[0]);
	wraparound_energy = get_wraparound_energy(rapl_unit.energy);
}

void ProfileDealloc()
{
	free(fd);
	free(parameters);
}

void EnergyStatCheck(EnergyStats stats_per_socket[num_pkg])
{
	struct timeval timestamp;

	for (int i = 0; i < num_pkg; i++)
	{
		stats_per_socket[i].socket = i+1;
		stats_per_socket[i].pkg = read_Pkg(i);
		stats_per_socket[i].core = read_Core(i);

		switch(power_domains_supported) {
			case READ_FROM_DRAM_AND_GPU:
				stats_per_socket[i].dram = read_Dram(i);
				stats_per_socket[i].gpu = read_Gpu(i);
				break;

			case READ_FROM_DRAM:
				stats_per_socket[i].dram = read_Dram(i);
				stats_per_socket[i].gpu = -1;
				break;

			case READ_FROM_GPU:
				stats_per_socket[i].dram = -1;
				stats_per_socket[i].gpu = read_Gpu(i);
				break;

			case UNDEFINED_ARCHITECTURE:
				fprintf(stderr,"ERROR: Architecture not found: %X\n",cpu_model);
				break;
		}

		gettimeofday(&timestamp,NULL);
		stats_per_socket[i].timestamp = timestamp;

	}

}

//static void copy_to_string(EnergyStats stats_per_socket[num_pkg], char ener_info[512])
//{
////<<<<<<< HEAD
////  	bzero(ener_info, 512);
////	int offset = 0;
////
////	char buffer[100];
////	int buffer_len;
////
////	for (int i = 0; i < num_pkg; i++) {
////		EnergyStats stats = stats_per_socket[i];
////		energy_stats_to_string(stats, buffer);
////		buffer_len = strlen(buffer);
////		memcpy(ener_info + offset, buffer, buffer_len);
////		offset += buffer_len;
////=======
//	for (int i = 0; i < num_pkg; i++) {
//		EnergyStats stats = stats_per_socket[i];
//		ener_info += energy_stats_to_string(stats, ener_info, power_domains_supported);
////>>>>>>> master
//	}
//}
//
//void ProfileDealloc()
//{
//	free(fd);
//	free(parameters);
//}
//
//JNIEXPORT void JNICALL Java_jRAPL_EnergyManager_profileInit(JNIEnv *env, jclass jcls)
//{	
//	ProfileInit();
//}
//
////assumes profile has already been inited. @TODO try to get this to be independent of profileinit and move it into arch_spec.c
//JNIEXPORT jint JNICALL Java_jRAPL_ArchSpec_getWraparoundEnergy(JNIEnv* env, jclass jcls)
//{
//	return (jint)wraparound_energy;
//}
//
//JNIEXPORT jstring JNICALL Java_jRAPL_EnergyMonitor_energyStatCheck(JNIEnv *env, jclass jcls) {
//	
//	char ener_info[512];
//	EnergyStats stats_per_socket[num_pkg];
//
//	EnergyStatCheck(stats_per_socket);
//	copy_to_string(stats_per_socket, ener_info);
//	
//	
//	jstring ener_string = (*env)->NewStringUTF(env, ener_info);
//  	
//	return ener_string;
//
//}
//
//JNIEXPORT void JNICALL Java_jRAPL_EnergyManager_profileDealloc(JNIEnv * env, jclass jcls) {
//
//	ProfileDealloc();
//
//}
//>>>>>>> master
