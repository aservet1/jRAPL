#include "msr.h"
#include <stdbool.h>
#include "CPUScaler_TimingUtils.h"
#include<sys/time.h>

#define MSR_DRAM_ENERGY_UNIT 0.000015

rapl_msr_parameter *parameters;
int *fd;
uint64_t num_pkg;
bool timingFunctionCalls = false;
bool timingMsrReadings = false;


struct timeval start, end, diff;

void copy_to_string(char *ener_info, char uncore_buffer[60], int uncore_num, char cpu_buffer[60], int cpu_num, char package_buffer[60], int package_num, int i, int *offset);
rapl_msr_unit get_rapl_unit();
void initialize_energy_info(char gpu_buffer[num_pkg][60], char dram_buffer[num_pkg][60], char cpu_buffer[num_pkg][60], char package_buffer[num_pkg][60]);

