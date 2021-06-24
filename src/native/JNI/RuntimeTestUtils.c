#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/time.h>

#include "ArchSpec.h"
#include "EnergyCheckUtils.h"
#include "JNIFunctionDeclarations.h"

//timestamping macros
#define STARTSTAMP	gettimeofday(&start, NULL);
#define STOPSTAMP	gettimeofday(&end, NULL); timersub(&end, &start, &diff);
#define DIFF_USEC	diff.tv_sec*1000000 + diff.tv_usec

// global static timestamp variables make this not thread safe
static struct timeval start, end, diff;
static int num_sockets;
static int power_domains_supported;
static int* fd;

JNIEXPORT void JNICALL Java_jRAPL_RuntimeTestUtils_initCSideTiming(JNIEnv* env, jclass jcls) {
	num_sockets = getSocketNum();
	power_domains_supported = get_power_domains_supported(get_micro_architecture());
	fd = (int *) malloc(num_sockets * sizeof(int));
	uint64_t num_pkg_thread = get_num_pkg_thread();
	char msr_filename[BUFSIZ];
	int core = 0;
	for(int i = 0; i < num_sockets; i++) {
		if(i > 0) {
			core += num_pkg_thread / 2; 	//measure the first core of each package
		}
		sprintf(msr_filename, "/dev/cpu/%d/msr", core);
		fd[i] = open(msr_filename, O_RDONLY);
	}
}

JNIEXPORT void JNICALL Java_jRAPL_RuntimeTestUtils_deallocCSideTiming(JNIEnv* env, jclass jcls) {
	for (int i = 0; i < num_sockets; i++) {
		close(fd[i]);
	}
	free(fd);
}

JNIEXPORT jlong JNICALL Java_jRAPL_RuntimeTestUtils_usecTimeProfileInit(JNIEnv* env, jclass jcls) {
	STARTSTAMP;
	Java_jRAPL_EnergyManager_profileInit(env, jcls);
	STOPSTAMP;
	return DIFF_USEC;
}

JNIEXPORT jlong JNICALL Java_jRAPL_RuntimeTestUtils_usecTimeEnergyStatCheck(JNIEnv* env, jclass jcls) {
	STARTSTAMP;
	Java_jRAPL_EnergyMonitor_energyStatCheck(env, jcls);
	STOPSTAMP;
	return DIFF_USEC;
}

JNIEXPORT jlong JNICALL Java_jRAPL_RuntimeTestUtils_usecTimeProfileDealloc(JNIEnv* env, jclass jcls) {
	STARTSTAMP;
	Java_jRAPL_EnergyManager_profileDealloc(env, jcls);
	STOPSTAMP;
	return DIFF_USEC;
}

#define DRAM 1
#define GPU 2
#define CORE 3
#define PKG 4

#define RETURN_EMPTY_ARRAY						\
	for (int i = 0; i < num_sockets; i++) fill[i] = -1;			\
	jlongArray result = (*env)->NewLongArray(env, num_sockets);		\
	if (result == NULL) return NULL;				\
	(*env)->SetLongArrayRegion(env, result, 0, num_sockets, fill);	\
	return result;

JNIEXPORT jlongArray JNICALL Java_jRAPL_RuntimeTestUtils_usecTimeMSRRead(JNIEnv* env, jclass jcls, jint which_power_domain) {
	jlong fill[num_sockets];
	int which_msr;
	switch (which_power_domain) {
		case DRAM:
			if (power_domains_supported != DRAM_GPU_CORE_PKG && power_domains_supported != DRAM_CORE_PKG) {
				RETURN_EMPTY_ARRAY;
			}
			which_msr = MSR_DRAM_ENERGY_STATUS;
			break;
		case GPU:
			if (power_domains_supported != DRAM_GPU_CORE_PKG && power_domains_supported != GPU_CORE_PKG) {
				RETURN_EMPTY_ARRAY;
			}
			which_msr = MSR_PP1_ENERGY_STATUS;
			break;
		case CORE:
			which_msr = MSR_PP0_ENERGY_STATUS;
			break;
		case PKG:
			which_msr = MSR_PKG_ENERGY_STATUS;
			break;
		default:
			fprintf(stderr,"invalid power domain request for usecTimeMSRREad: %d\n",which_power_domain);
			return NULL;
	}
	
	for (int i = 0; i < num_sockets; i++) {
		STARTSTAMP;
		read_msr(fd[i],which_msr);
		STOPSTAMP;
		fill[i] = DIFF_USEC;
	}

	jlongArray result = (*env)->NewLongArray(env, num_sockets);
	if (result == NULL) return NULL;
	(*env)->SetLongArrayRegion(env, result, 0, num_sockets, fill);
	return result;
}

static struct timeval tvStart, tvStop;
JNIEXPORT void JNICALL Java_jRAPL_RuntimeTestUtils_ctimeStart(JNIEnv* env, jclass jcls) {
	gettimeofday(&tvStart,0x0);
}
JNIEXPORT void JNICALL Java_jRAPL_RuntimeTestUtils_ctimeStop(JNIEnv* env, jclass jcls) {
	gettimeofday(&tvStop,0x0);
}
JNIEXPORT jlong JNICALL Java_jRAPL_RuntimeTestUtils_ctimeElapsedUsec(JNIEnv* env, jclass jcls) {
	return (jlong) ((tvStop.tv_sec*1000000+tvStop.tv_usec) - (tvStart.tv_sec*1000000+tvStart.tv_usec));
}
