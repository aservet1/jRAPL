#include<stdio.h>
#include<stdlib.h>
#include <fcntl.h>
#include<sys/time.h>

#include "ArchSpec.h"
#include "EnergyCheckUtils.h"

//timestamping macros
#define STARTSTAMP	gettimeofday(&start, NULL);
#define STOPSTAMP	gettimeofday(&end, NULL); timersub(&end, &start, &diff);
#define DIFF_USEC	diff.tv_sec*1000000 + diff.tv_usec

static struct timeval start, end, diff;
static int num_sockets;
static int dram_or_gpu;
static int* fd;

JNIEXPORT void JNICALL Java_jRAPL_RuntimeTestUtils_initCSideTiming(JNIEnv* env, jclass jcls) {
	num_sockets = getSocketNum();
	dram_or_gpu = get_power_domains_supported(get_cpu_model(),NULL);
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
			if (dram_or_gpu != READ_FROM_DRAM_AND_GPU && dram_or_gpu != READ_FROM_DRAM) {
				RETURN_EMPTY_ARRAY;
			}
			which_msr = MSR_DRAM_ENERGY_STATUS;
			break;

		case GPU:
			if (dram_or_gpu != READ_FROM_DRAM_AND_GPU && dram_or_gpu != READ_FROM_GPU) {
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
