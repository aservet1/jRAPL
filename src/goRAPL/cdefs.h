#ifndef CDEFS_H
#define CDEFS_H

#include <stdio.h>
#include <assert.h>

#include "arch_spec.h"
#include "energy_check_utils.h"

EnergyStats
energyStatCheckPerSocket(int Socket) {
	assert ( Socket-1 < getSocketNum() );
	ProfileInit();
	EnergyStats stats_per_socket[getSocketNum()];
	EnergyStatCheck(stats_per_socket);
	ProfileDealloc();
	return stats_per_socket[Socket-1];
}

#endif // CDEFS_H
