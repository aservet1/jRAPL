#include <stdio.h>
#include <jni.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
#include <stdint.h>
#include <string.h>
#include <inttypes.h>
#include <sys/time.h>
#include <sys/types.h>
#include <stdbool.h>

#include "CPUScaler.h"
#include "arch_spec.h"
#include "msr.h"
#include "CPUScaler_TimingUtils.h"
#include "EnergyStats.h"

#define MSR_DRAM_ENERGY_UNIT 0.000015

static int architecture_category; //TODO - get better name for this variable
static uint32_t cpu_model;
static rapl_msr_unit rapl_unit;
static rapl_msr_parameter *parameters;
static int *fd;
static uint64_t num_pkg;
static bool timingFunctionCalls = false;
static bool timingMsrReadings = false;

static struct timeval start, end, diff;

//@TODO - undo this embedded timing monstrosity, just have your own native-interface file designated to calling these from the outside

#define START_TIMESTAMP_FUNCTIONCALLS	\
	if (timingFunctionCalls) gettimeofday(&start,NULL);

#define STOP_TIMESTAMP_FUNCTIONCALLS(name)	\
	if (timingFunctionCalls) {	\
		gettimeofday(&end,NULL);	\
		timeval_subtract(&diff, &end, &start);	\
		logTime( #name , diff.tv_sec*1000 + diff.tv_usec);	\
	}	

JNIEXPORT void JNICALL Java_jrapltesting_RuntimeTestUtils_StartTimeLogs(JNIEnv *env, jclass jcls, jint logSize, jboolean _timingFunctionCalls, jboolean _timingMsrReadings)
{
	timingFunctionCalls = _timingFunctionCalls;
	timingMsrReadings = _timingMsrReadings;
	initAllLogs(logSize);
}
JNIEXPORT void JNICALL Java_jrapltesting_RuntimeTestUtils_FinalizeTimeLogs(JNIEnv *env, jclass jcls)
{
	finalizeAllLogs();
}


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
	int wraparound_energy;

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
JNIEXPORT jint JNICALL Java_jrapl_JRAPL_ProfileInit(JNIEnv *env, jclass jcls) {
	START_TIMESTAMP_FUNCTIONCALLS;

	int wraparound_energy = ProfileInit();

	STOP_TIMESTAMP_FUNCTIONCALLS( ProfileInit() );
	return wraparound_energy;
}

/** <Alejandro's Interpretation>
 *	Return number of CPU sockets
 */
JNIEXPORT jint JNICALL Java_jrapl_ArchSpec_GetSocketNum(JNIEnv *env, jclass jcls) {

	START_TIMESTAMP_FUNCTIONCALLS;

	int socketNum = getSocketNum(); // in arch_spec.c

	STOP_TIMESTAMP_FUNCTIONCALLS( GetSocketNum() );

	return (jint)socketNum; 
}

JNIEXPORT jint JNICALL Java_jrapl_ArchSpec_DramOrGpu(JNIEnv * env, jclass jcls) {
	//@TODO -- set up timing utils, here and in CPUScaler_TimingUtils.c . also, change function name from architecture_category
	return get_architecture_category(get_cpu_model());

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
	double result = 0.0;

	for (int i = 0; i < num_pkg; i++) {
		pkg[i] = -1; pp0[i] = -1; pp1[i] = -1; dram[i] = -1; //sentintel values for no energy read into them bc we only read one of dram or gpu


		if (timingMsrReadings) gettimeofday(&start, NULL);
		result = read_msr(fd[i], MSR_PKG_ENERGY_STATUS);	//First 32 bits so don't need shift bits.
		pkg[i] = (double) result * rapl_unit.energy;
		if (timingMsrReadings) {
			gettimeofday(&end,NULL);
			timeval_subtract(&diff, &end, &start);
			logTime("PACKAGE Socket 0", diff.tv_sec*1000 + diff.tv_usec); //@TODO it should be "Socket (i)" but I didn't want to deal with string building in C and our copmuters only have 1 socket, but this definitely needs to be changed to scale up to 2 socket machines (jRAPL currently only works for 2 socket machines)
		}

		if (timingMsrReadings)gettimeofday(&start, NULL);
		result = read_msr(fd[i], MSR_PP0_ENERGY_STATUS);
		pp0[i] = (double) result * rapl_unit.energy;
		if (timingMsrReadings) {
			gettimeofday(&end,NULL);
			timeval_subtract(&diff, &end, &start);
			logTime("CORE Socket 0", diff.tv_sec*1000 + diff.tv_usec); //@TODO it should be "Socket (i)" but I didn't want to deal with string building in C and our copmuters only have 1 socket, but this definitely needs to be changed to scale up to 2 socket machines (jRAPL currently only works for 2 socket machines)
		}


		switch(architecture_category) {
			case READ_FROM_DRAM:
				if (timingMsrReadings) gettimeofday(&start, NULL);
				result = read_msr(fd[i],MSR_DRAM_ENERGY_STATUS);
				if (timingMsrReadings) {
					gettimeofday(&end,NULL);
					timeval_subtract(&diff, &end, &start);
					logTime("DRAM Socket 0", diff.tv_sec*1000 + diff.tv_usec); //@TODO it should be "Socket (i)" but I didn't want to deal with string building in C and our copmuters only have 1 socket, but this definitely needs to be changed to scale up to 2 socket machines (jRAPL currently only works for 2 socket machines)
				}
				if (cpu_model == BROADWELL || cpu_model == BROADWELL2) {
					dram[i] =(double)result*MSR_DRAM_ENERGY_UNIT;
				} else {
					dram[i] =(double)result*rapl_unit.energy;
				}

				/*Insert socket number*/

				break;
			case READ_FROM_GPU:
				if (timingMsrReadings) gettimeofday(&start, NULL);
				result = read_msr(fd[i],MSR_PP1_ENERGY_STATUS);
				if (timingMsrReadings) {
					gettimeofday(&end,NULL);
					timeval_subtract(&diff, &end, &start);
					logTime("GPU Socket 0", diff.tv_sec*1000 + diff.tv_usec); //@TODO it should be "Socket (i)" but I didn't want to deal with string building in C and our copmuters only have 1 socket, but this definitely needs to be changed to scale up to 2 socket machines (jRAPL currently only works for 2 socket machines)
				}
				pp1[i] = (double) result *rapl_unit.energy;


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
void copy_to_string(EnergyStats stats_per_socket[num_pkg], char ener_info[512])
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
	
	START_TIMESTAMP_FUNCTIONCALLS;
	
	char ener_info[512];
	EnergyStats stats_per_socket[num_pkg];

	EnergyStatCheck(stats_per_socket);
	copy_to_string(stats_per_socket, ener_info);

	jstring ener_string = (*env)->NewStringUTF(env, ener_info);
  	
	STOP_TIMESTAMP_FUNCTIONCALLS( EnergyStatCheck() );
	
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
	START_TIMESTAMP_FUNCTIONCALLS;

	ProfileDealloc();

	STOP_TIMESTAMP_FUNCTIONCALLS( ProfileDealloc() );
}
