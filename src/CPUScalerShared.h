/*

#ifndef _CPU_SCALER_SHARED_H
#define _CPU_SCALER_SHARED_H

#include <sys/time.h>
#include <sys/types.h>
#include <stdbool.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
#include <stdint.h>
#include <string.h>
#include <inttypes.h>
#include "msr.h"
#include "arch_spec.h"

extern int *fd;
extern uint64_t num_pkg;
extern bool timingFunctionCalls = false;
extern bool timingMsrReadings = false;

struct timeval start, end, diff;

void copy_to_string(char *ener_info, char uncore_buffer[60], int uncore_num, char cpu_buffer[60], int cpu_num, char package_buffer[60], int package_num, int i, int *offset);
rapl_msr_unit get_rapl_unit();
void initialize_energy_info(char gpu_buffer[num_pkg][60], char dram_buffer[num_pkg][60], char cpu_buffer[num_pkg][60], char package_buffer[num_pkg][60]);
char* EnergyStatCheck_C(char *ener_info);

#endif

*/
