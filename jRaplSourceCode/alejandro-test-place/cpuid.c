#include <stdio.h>
//#include <jni.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
#include <stdint.h>
#include <string.h>


uint32_t
get_cpu_model(void)
{
	uint32_t eax, ebx, ecx, edx;
    eax = 0x01;

    __asm__ volatile ("cpuid"     
			: "=a" (eax),         
			"=b" (ebx),           
			"=c" (ecx),          
			"=d" (edx)            
			: "0" (eax), "2" (ecx));

	return (((eax>>16)&0xFU)<<4) + ((eax>>4)&0xFU);
}

int main(){printf("%x\n",get_cpu_model());}
