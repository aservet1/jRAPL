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
#include "CPUScalerShared.h"

/// Comments starting with '///' are my (Alejandro's) notes to self.
/// None of this is official documentation.

#define MSR_DRAM_ENERGY_UNIT 0.000015

static rapl_msr_parameter *parameters;
static int *fd;
static uint64_t num_pkg;
static bool timingFunctionCalls = false;
static bool timingMsrReadings = false;

static struct timeval start, end, diff;

#define START_TIMESTAMP_FUNCTIONCALLS	\
	if (timingFunctionCalls) gettimeofday(&start,NULL);

#define STOP_TIMESTAMP_FUNCTIONCALLS(name)	\
	if (timingFunctionCalls) {	\
		gettimeofday(&end,NULL);	\
		timeval_subtract(&diff, &end, &start);	\
		logTime( #name , diff.tv_sec*1000 + diff.tv_usec);	\
	}	


JNIEXPORT void JNICALL Java_jrapl_RuntimeTestUtils_StartTimeLogs(JNIEnv *env, jclass jcls, jint logSize, jboolean _timingFunctionCalls, jboolean _timingMsrReadings)
{
	timingFunctionCalls = _timingFunctionCalls;
	timingMsrReadings = _timingMsrReadings;
	initAllLogs(logSize);
}
JNIEXPORT void JNICALL Java_jrapl_RuntimeTestUtils_FinalizeTimeLogs(JNIEnv *env, jclass jcls)
{
	finalizeAllLogs();
}

/** <Alejandro's Interpretation>
 *	Sets up an an energy profile. (*?)What exactly is an energy profile? A bunch of data stored about the current energy state...
 *	reads and stores CPU model, socketnum. calculates wraparound energy.
 *  the 'fd' array is an array of which msr regs. num msr regs is number of packages the computer has
 *  initializes the rapl unit (stuff holding the conversions to translate msr data sections into meaningful 'human-readable' stuff)
 */
JNIEXPORT jint JNICALL Java_jrapl_JRAPL_ProfileInit(JNIEnv *env, jclass jcls) {
	START_TIMESTAMP_FUNCTIONCALLS;

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


	STOP_TIMESTAMP_FUNCTIONCALLS( ProfileInit() );
	return wraparound_energy;
}

/** <Alejandro's Interpretation>
 * Gets num of cpu sockets but casts it as a jint for the java end of things
 */
JNIEXPORT jint JNICALL Java_jrapl_ArchSpec_GetSocketNum(JNIEnv *env, jclass jcls) {

	START_TIMESTAMP_FUNCTIONCALLS;

	int socketNum = getSocketNum();

	STOP_TIMESTAMP_FUNCTIONCALLS( GetSocketNum() );

	return (jint)socketNum; 
}

JNIEXPORT jint JNICALL Java_jrapl_ArchSpec_DramOrGpu(JNIEnv * env, jclass jcls) {
	//@TODO -- set up timing utils, here and in CPUScaler_TimingUtils.c
	return get_architecture_category(get_cpu_model());

}

/** <Alejandro's Interpretation>
 * Makes a string from the energy info. Initializes energy info with that function above and
 *
 * The first entry is: Dram/uncore gpu energy (depends on the cpu architecture)
 * The second entry is: CPU energy
 * The third entry is: Package energy
 */
JNIEXPORT jstring JNICALL Java_jrapl_EnergyCheckUtils_EnergyStatCheck(JNIEnv *env, jclass jcls) {
	
	START_TIMESTAMP_FUNCTIONCALLS;

	jstring ener_string;
	char ener_info[512];
	EnergyStatCheck_C(ener_info);
	ener_string = (*env)->NewStringUTF(env, ener_info);
  	
	STOP_TIMESTAMP_FUNCTIONCALLS( EnergyStatCheck() );
	
	return ener_string;

}

/** <Alejandro's Interpretation>
 * Free memory allocated by profile init function
 */
JNIEXPORT void JNICALL Java_jrapl_JRAPL_ProfileDealloc(JNIEnv * env, jclass jcls) {
	START_TIMESTAMP_FUNCTIONCALLS;

	free(fd);
	free(parameters);

	STOP_TIMESTAMP_FUNCTIONCALLS( ProfileDealloc() );
}
