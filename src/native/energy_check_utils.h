
#ifndef ENERGY_CHECK_UTILS_H
#define ENERGY_CHECK_UTILS_H

#include "energy_stats.h"

void ProfileInit();
void EnergyStatCheck(EnergyStats stats_per_socket[]);
void ProfileDealloc();

int* get_msr_fds();

#endif //ENERGY_CHECK_UTILS_H
