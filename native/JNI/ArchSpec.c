#include <stdio.h>
#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include "ArchSpec.h"
#include "MSR.h"

JNIEXPORT jint JNICALL Java_jRAPL_ArchSpec_getWraparoundEnergy(JNIEnv* env, jclass jcls) {
	int fd = open("/dev/cpu/0/msr",O_RDONLY);
	int wraparound_energy = get_wraparound_energy(get_rapl_unit(fd).energy);
	close(fd);
	return wraparound_energy;
}

JNIEXPORT jstring JNICALL
Java_jRAPL_ArchSpec_energyStatsStringFormat(JNIEnv* env, jclass jcls) {
	char power_domain_string[512];
	get_power_domains_supported(get_cpu_model(),power_domain_string);
	return (*env)->NewStringUTF(env, power_domain_string);
	
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
