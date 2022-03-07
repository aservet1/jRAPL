#include <stdio.h>
#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "msr.h"
#include "arch_spec.h"
#include "platform_support.h"

JNIEXPORT jstring JNICALL
Java_jRAPL_NativeAccess_getEnergySampleArrayOrder(JNIEnv* env, jclass jcls) {
	char* order;
    power_domain_support_info_t pds = get_power_domains_supported();
	if(pds.dram && pds.pp1 && pds.pp0 && pds.pkg)  { order = "dram,pp1,pp0,pkg";       }
	else if(pds.dram && pds.pp0 && pds.pkg)        { order = "dram,pp0,pkg";           }
	else if(pds.pp1 && pds.pp0 && pds.pkg)         { order = "pp1,pp0,pkg";            }
	else                                           { order = "undefined_architecture"; }

    return (*env)->NewStringUTF(env, order);
}

JNIEXPORT jdouble JNICALL
Java_jRAPL_NativeAccess_getWraparoundEnergy(JNIEnv* env, jclass jcls) {
	int fd = open("/dev/cpu/0/msr",O_RDONLY);
	double wraparound_energy = get_rapl_wraparound(get_rapl_unit(fd).energy);
	close(fd);
	return wraparound_energy;
}

JNIEXPORT jdouble JNICALL
Java_jRAPL_NativeAccess_getDramWraparoundEnergy(JNIEnv* env, jclass jcls) {
	int fd = open("/dev/cpu/0/msr",O_RDONLY);
	double dram_wraparound_energy = get_rapl_wraparound (
		(  is_this_the_current_architecture("BROADWELL") || is_this_the_current_architecture("BROADWELL2") )
			? BROADWELL_MSR_DRAM_ENERGY_UNIT
			: get_rapl_unit(fd).energy
	);
	close(fd);
	return dram_wraparound_energy;
}

JNIEXPORT jint JNICALL
Java_jRAPL_NativeAccess_getSocketNum(JNIEnv *env, jclass jcls) {
	return (jint)getSocketNum(); 
}

JNIEXPORT jint JNICALL
Java_jRAPL_NativeAccess_getMicroArchitectureID(JNIEnv* env, jclass jcls) {
	return get_micro_architecture();
}

JNIEXPORT jstring JNICALL
Java_jRAPL_NativeAccess_getMicroArchitectureName(JNIEnv* env, jclass jcls) {
	char name_buffer[64];
    get_arch_name(name_buffer);
	return (*env)->NewStringUTF(env, name_buffer);
}

