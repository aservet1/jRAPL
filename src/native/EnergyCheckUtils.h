
#ifndef ENERGY_CHECK_UTILS_H
#define ENERGY_CHECK_UTILS_H

#include "EnergyStats.h"

void ProfileInit();
void EnergyStatCheck(EnergyStats stats_per_socket[]);
void ProfileDealloc();

int* get_msr_fds();

// the below two are just to probe and test, not currently incorporated into the actual RAPL interface being provided
void ProfileInitAllCores(int num_readings);
void ProfileDeallocAllCores();

#endif //ENERGY_CHECK_UTILS_H
