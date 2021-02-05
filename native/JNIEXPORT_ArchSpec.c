#include <stdio.h>
#include <jni.h>
//#include <stdlib.h>
//#include <fcntl.h>
//#include <unistd.h>
//#include <math.h>
//#include <stdint.h>
//#include <string.h>
#include "ArchSpec.h"

//assumes profile has already been inited. @TODO try to get this to be independent of profileinit and move it into arch_spec.c
JNIEXPORT jint JNICALL Java_jRAPL_ArchSpec_getWraparoundEnergy(JNIEnv* env, jclass jcls)
{
	return 9999; //@TODO actually make this the accurate one from open()ing and close()ing the msr fd, making sure not to interfere with it being open in the other thing from ProfileInit();      //(jint)wraparound_energy;
}
//TODO -- for organization, see if you can do the wraparound energy calculation here
//	instead of EnergyCheckUtils.c involves open()-ing up the msr and closing it (if not already open)
//  and reading directly from it. that would make it so you don't have to do ProfileInit()
//  if you just want to read the wraparound energy real quick

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
