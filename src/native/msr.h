#ifndef MSR
#define MSR
#define _XOPEN_SOURCE 500 //for pread and pwrite

#include <stdint.h>

/**
 * Reference Intel ® 64 and IA-32 Architectures Software Developer’s Manual
 * for those CPUID information (December 2016)
 */
#define MSR_RAPL_POWER_UNIT				0x606


#define BROADWELL_MSR_DRAM_ENERGY_UNIT 0.000015


/**Energy measurement**/
#define MSR_PP0_ENERGY_STATUS			0x639 //read to core_buffer[i] in EnergyCheckUtils.c
#define MSR_PP1_ENERGY_STATUS     0x641 //read to gpu_buffer[i] in EnergyCheckUtils.c
#define MSR_PKG_ENERGY_STATUS			0x611 //read to package[i] in EnergyCheckUtils.c (total energy usage)
#define MSR_DRAM_ENERGY_STATUS		0x619 //read to dram_buffer[i] in EnergyCheckUtils.c

/**Power/time window maximum/minimum information(Only support for PKG and DRAM **/
#define MSR_PKG_POWER_INFO				0x614
#define MSR_DRAM_POWER_INFO     	0x61C

/**Power limit**/
#define MSR_PKG_POWER_LIMIT       0x610
#define MSR_DRAM_POWER_LIMIT      0x618
#define MSR_PP0_POWER_LIMIT       0x638
#define MSR_PP1_POWER_LIMIT       0x640

/*Power domains*/
#define PKG_DOMAIN	0
#define DRAM_DOMAIN	1

/*Power limit set*/
#define DISABLE 0
#define ENABLE 1


//---------Alejandro's recent #defines---------------//
//@TODO group them with reasonable /**Descriptions**/ like the ones above
//@TODO Should the _SIZE ones of width 1 have a special name to denote that it's just one bit? Seems possibly important
#define MSR_SIZE        64
#define DELTA_MAX		5000000000.0

#define POWER_LIMIT_SIZE    14
#define POWER_LIMIT_START   0

#define	LIMIT_ENABLE_SIZE   1
#define LIMIT_ENABLE_START  15

#define LOCK_ENABLE_SIZE    1
#define LOCK_ENABLE_START   63

#define PKG_POWER_LIMIT_SETTING_SIZE    1
#define PKG_POWER_LIMIT_SET_1_START 15
#define PKG_POWER_LIMIT_SET_2_START 47

#define DRAM_POWER_LIMIT_SETTING_SIZE    1
#define DRAM_POWER_LIMIT_SETTING_START  15

#define CLAMP_ENABLE_SIZE           1
#define CLAMP_SET1_ENABLE_START    16
#define CLAMP_SET2_ENABLE_START    48

#define F_SIZE                   2
#define F_START_TIMEWINDOW_1    22
#define F_START_TIMEWINDOW_2    54
#define Y_SIZE                   5
#define Y_START_TIMEWINDOW_1    17
#define Y_START_TIMEWINDOW_2    49

#define POWER_LIMIT_FIELD_SIZE      15
#define POWER_LIMIT_START_LOW_END    0
#define POWER_LIMIT_START_HIGH_END  32


#define POWER_BIT_SIZE      4
#define POWER_BIT_START     0

#define ENERGY_BIT_SIZE     5
#define ENERGY_BIT_START    8

#define TIME_BIT_SIZE       4
#define	TIME_BIT_START     16


#define POWER_INFO_FIELD_SIZE       15
#define THERMAL_SPEC_POWER_START     0
#define MIN_POWER_START             16
#define MAX_POWER_START             32
#define MAX_TIME_WINDOW_SIZE         6
#define MAX_TIME_WINDOW_START       32
//------------------------------------------------------------//


/***global variable***/
typedef struct rapl_msr_unit {
	double power;
	double energy;
	double time;
} rapl_msr_unit;

typedef struct rapl_msr_parameter {
	double thermal_spec_power;
	double min_power;
	double max_power;
	double max_time_window;
} rapl_msr_parameter;

typedef struct rapl_msr_power_limit_t {
	double power_limit;
	/* time_window_limit = 2^Y*F
	 * F(23:22) Y(21:17)
	 */
	double time_window_limit;
	uint64_t clamp_enable;
	uint64_t limit_enable;
	uint64_t lock_enable;
} rapl_msr_power_limit_t;

////deglobalized(?)
/*extern char *ener_info;
extern rapl_msr_unit rapl_unit;
extern int *fd;
extern rapl_msr_parameter *parameters;*/


typedef enum {
	MINIMUM_POWER_LIMIT = 0,
	MAXIMUM_POWER_LIMIT,
	COSTOM_POWER_LIMIT
} msr_power_set;

typedef enum {
	NA = 0,
	MAXIMUM_TIME_WINDOW,
	COSTOM_TIME_WINDOW
} msr_time_window_set;

#define _2POW(e) ((e == 0) ? 1 : (2 << (e - 1)))

double
calc_time_window(uint64_t Y, uint64_t F);

void
calc_y(uint64_t *Y, uint64_t F, double custm_time);

rapl_msr_power_limit_t
get_specs(int fd, uint64_t addr);

void
set_dram_power_limit_enable(int fd, uint64_t setting, uint64_t addr);

void
set_package_power_limit_enable(int fd, uint64_t setting, uint64_t addr);

void
set_package_clamp_enable(int fd, uint64_t setting, uint64_t addr);

void
convert_optimal_yf_from_time(uint64_t *Y, uint64_t *F, double custm_power);
void
set_pkg_time_window_limit(int fd, uint64_t addr, double custm_time);

void
set_dram_time_window_limit(int fd, uint64_t addr, double custm_time);

void
set_pkg_power_limit(int fd, uint64_t addr, double custm_power);

void
set_dram_power_limit(int fd, uint64_t addr, double custm_power);

uint64_t
extractBitField(uint64_t inField, uint64_t width, uint64_t offset);

uint64_t
read_msr(int fd, uint64_t which);

void
write_msr(int fd, uint64_t which, uint64_t limit_info);

double get_wraparound_energy(double energy_unit);

void get_msr_unit(rapl_msr_unit *unit_obj, uint64_t data);

void get_rapl_pkg_parameters(int fd, rapl_msr_unit *unit_obj, rapl_msr_parameter *paras);

void get_rapl_dram_parameters(int fd, rapl_msr_unit *unit_obj, rapl_msr_parameter *paras);

void get_rapl_parameters(int fd, uint64_t msr_addr, rapl_msr_unit *unit_obj, rapl_msr_parameter *paras);

void getPowerSpec(double result[4], rapl_msr_parameter *parameter, int domain);

#endif
