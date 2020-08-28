
#ifndef CPUSCALER_H
#define CPUSCALER_H

#include "EnergyStats.h"

int ProfileInit();

void EnergyStatCheck(EnergyStats stats_per_socket[]);

void ProfileDealloc();

#endif //CPUSCALER_H
