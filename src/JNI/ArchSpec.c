#include <stdio.h>
#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "msr.h"
#include "arch_spec.h"

JNIEXPORT jstring JNICALL
Java_jRAPL_NativeAccess_getEnergySampleArrayOrder(JNIEnv* env, jclass jcls) {
	char* order;
	switch(get_power_domains_supported(get_micro_architecture())) {
		case DRAM_GPU_CORE_PKG:
			order = "dram,gpu,core,pkg";
			break;
		case DRAM_CORE_PKG:
			order = "dram,core,pkg";
			break;
		case GPU_CORE_PKG:
			order = "gpu,core,pkg";
			break;
		default:
			order = "undefined_architecture";
	}
	return (*env)->NewStringUTF(env, order);
}

JNIEXPORT jdouble JNICALL
Java_jRAPL_NativeAccess_getWraparoundEnergy(JNIEnv* env, jclass jcls) {
	int fd = open("/dev/cpu/0/msr",O_RDONLY);
	double wraparound_energy = get_wraparound_energy(get_rapl_unit(fd).energy);
	close(fd);
	return wraparound_energy;
}
JNIEXPORT jdouble JNICALL
Java_jRAPL_NativeAccess_getDramWraparoundEnergy(JNIEnv* env, jclass jcls) {
	int fd = open("/dev/cpu/0/msr",O_RDONLY);
	int microarch = get_micro_architecture();
	double wraparound_energy = get_wraparound_energy (
		(microarch == BROADWELL || microarch == BROADWELL2)
			? BROADWELL_MSR_DRAM_ENERGY_UNIT
			: get_rapl_unit(fd).energy
	);
	close(fd);
	return wraparound_energy;
}

// JNIEXPORT jstring JNICALL
// Java_jRAPL_NativeAccess_energyStatsStringFormat(JNIEnv* env, jclass jcls) {
// 	char energy_stats_jni_string_format[512];
// 	get_energy_stats_jni_string_format(energy_stats_jni_string_format);
// 	// get_power_domains_supported(get_cpu_model(),power_domain_string);
// 	return (*env)->NewStringUTF(env, energy_stats_jni_string_format);	
// }

JNIEXPORT jint JNICALL
Java_jRAPL_NativeAccess_getSocketNum(JNIEnv *env, jclass jcls) {
	return (jint)getSocketNum(); 
}

JNIEXPORT jint JNICALL
Java_jRAPL_NativeAccess_getMicroArchitecture(JNIEnv* env, jclass jcls) {
	return get_micro_architecture();
}

JNIEXPORT jstring JNICALL
Java_jRAPL_NativeAccess_getMicroArchitectureName(JNIEnv* env, jclass jcls) {
	const char* name;
	switch(get_micro_architecture()) {
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
		default: name = "UNDEFINED_MICROARCHITECTURE";
	}
	return (*env)->NewStringUTF(env, name);
}
