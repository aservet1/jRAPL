#include <stdio.h>
#include <math.h>

#include "energy_check_utils.h"
#include "msr.h"
#include "arch_spec.h"

/** Note that when reaserching MSR, it stands for Model-Specific Register, not to be
 *    confused with Machine State Register
 *
 *  Great info found on https://software.intel.com/sites/default/files/managed/39/c5/325462-sdm-vol-1-2abcd-3abcd.pdf
 *    starting on page 3207
 */

//factor of F for time_window_limit. It represents these four value.
static double F_arr[4] = {1.0, 1.25, 1.50, 1.75};
static rapl_msr_unit rapl_unit;

// Bit twiddling logic for value about to be written to MSR
void
putBitField(uint64_t inField, uint64_t *data, uint64_t width, uint64_t offset)
{
	uint64_t mask = ~0;
	uint64_t bitMask;

	/*The bits to be overwritten are located in the leftmost part.*/
	if ((offset+width) == MSR_SIZE)
	{
        	bitMask = (mask<<offset);
    	} else {
		bitMask = (mask<<offset) ^ (mask<<(offset + width));
	}
	/*Reset the bits in *data that will be overwritten to be 0, and keep other bits the same.*/
	*data = ~bitMask & *data;
    /*Overwrite the cleared bits with new ones provided in inField*/
	*data = *data | (inField<<offset);
}


// Bit twiddling logic to extract bits from a recent MSR read
uint64_t
extractBitField(uint64_t inField, uint64_t width, uint64_t offset)
{
	uint64_t mask = ~0;
	uint64_t bitMask;
	uint64_t outField;

	if ((offset+width) == MSR_SIZE)  {
		bitMask = (mask<<offset);
	} else {
		bitMask = (mask<<offset) ^ (mask<<(offset+width));
	}

	outField = (inField & bitMask) >> offset;
	return outField;
}

uint64_t read_msr(int fd, uint64_t msrOffset)
{
	uint64_t data = 0;
	if ( pread(fd, &data, sizeof data, msrOffset) != sizeof data ) {
	  // fprintf(stderr,"ERROR reading from MSR.\n"
      //   "Most often caused by not running this program as root,\n"
      //   "or because MSR kernel module is not enabled. For the latter\n"
      //   "circumstance, use the command `sudo modprobe msr` \n.");
      return -1;
	}
	return data;
}

void write_msr(int fd, uint64_t msrOffset, uint64_t limit_info)
{
	if ( pwrite(fd, &limit_info , sizeof limit_info, msrOffset) != sizeof limit_info) {
	  fprintf(stderr,"ERROR writing to MSR.\n"
        "Most often caused by not running this program as root,\n"
        "or because MSR kernel module is not enabled. For the latter\n"
        "circumstance, use the command `sudo modprobe msr` \n.");
	}
}

/** RAPL conversion unit */
rapl_msr_unit
get_rapl_unit(int msr_fd)
{
	rapl_msr_unit rapl_unit;
	uint64_t unit_info = read_msr(msr_fd, MSR_RAPL_POWER_UNIT);
	get_msr_unit(&rapl_unit, unit_info);
	return rapl_unit;
}
void
set_rapl_unit_for_general_MSR_interface_functions_to_reference(int fd) {
	rapl_unit = get_rapl_unit(fd);
}

/**
 * Calculates the actual time window from the bits stored in the time window field
 * Formula from Intel Manual: Actual time window value = 2^Y * (1.0 + Z/4.0) * TimeWindowBits.
 */
double calc_time_window(uint64_t Y, uint64_t F) {
	return _2POW(Y) * F_arr[F] * rapl_unit.time;
}

/* <--- Alejandro's Interpretation --->
 * Calculate real Y and F from how they are represented in MSR bits (see Intel Manual MSR API for what Y and F are)
 */
void
calc_y(uint64_t *Y, uint64_t F, double custm_time) {
	*Y = log2(custm_time / rapl_unit.time / F_arr[F]);
}

/* <--- Alejandro's Interpretation --->
 * Exracts specs from current state of MSR and stores them in a rapl_msr_power_timit_t struct.
 * Power limit, time window limit, clamp enable, limit enable, lock enable.
 */
rapl_msr_power_limit_t
get_specs(int fd, uint64_t addr) {
	uint64_t msr;
	rapl_msr_power_limit_t limit_info;
	msr = read_msr(fd, addr);
	limit_info.power_limit = rapl_unit.power * extractBitField(msr, POWER_LIMIT_SIZE, POWER_LIMIT_START);
	limit_info.time_window_limit = calc_time_window(extractBitField(msr, Y_SIZE, Y_START_TIMEWINDOW_1), extractBitField(msr, F_SIZE, F_START_TIMEWINDOW_1));
	limit_info.clamp_enable = extractBitField(msr, CLAMP_ENABLE_SIZE, CLAMP_SET1_ENABLE_START);
	limit_info.limit_enable = extractBitField(msr, LIMIT_ENABLE_SIZE, LIMIT_ENABLE_START);
	limit_info.lock_enable = extractBitField(msr, LOCK_ENABLE_SIZE, LOCK_ENABLE_START);
	return limit_info;
}

/* <--- Alejandro's Interpretation --->
 * Enable or disable the package power limit stored in MSR. Enabled = 1, Disabled = 0
 */
void
set_package_power_limit_enable(int fd, uint64_t setting, uint64_t addr) {
	uint64_t msr;
	msr = read_msr(fd, addr);

	//enable set #1
	putBitField(setting, &msr, PKG_POWER_LIMIT_SETTING_SIZE, PKG_POWER_LIMIT_SET_1_START); ////possibly rethink name
	//enable set #2
	putBitField(setting, &msr, PKG_POWER_LIMIT_SETTING_SIZE, PKG_POWER_LIMIT_SET_2_START); ////possibly rethink name
	write_msr(fd, addr, msr);

}

/* <--- Alejandro's Interpretation --->
 * Enable or disable the dram power limit stored in MSR. Enabled = 1, Disabled = 0
 */
void
set_dram_power_limit_enable(int fd, uint64_t setting, uint64_t addr) {
	uint64_t msr;
	msr = read_msr(fd, addr);

	//enable set
	putBitField(setting, &msr, DRAM_POWER_LIMIT_SETTING_SIZE, DRAM_POWER_LIMIT_SETTING_START); ////possibly rethink name

	write_msr(fd, addr, msr);

}

void
set_package_clamp_enable(int fd, uint64_t setting, uint64_t addr) {
	uint64_t msr;
	msr = read_msr(fd, addr);

	//clamp set #1
	putBitField(setting, &msr, CLAMP_ENABLE_SIZE, CLAMP_SET1_ENABLE_START);
	//clamp set #2
	putBitField(setting, &msr, CLAMP_ENABLE_SIZE, CLAMP_SET2_ENABLE_START);
	//putBitField(power_limit, &msr, 15, 32);

	write_msr(fd, addr, msr);

}

//This idea is loop four possible sets of Y and F, and in return to get
//the time window, then use the set of Y and F that is smaller than but
//closest to the customized time.
void
convert_optimal_yf_from_time(uint64_t *Y, uint64_t *F, double custm_time) {
	uint64_t temp_y;
	double time_window = 0.0;
	double delta = 0.0;
	double smal_delta = DELTA_MAX;
	int i = 0;
	for(i = 0; i < 4; i++) {
		calc_y(&temp_y, i, custm_time);
		time_window = calc_time_window(temp_y, i);
		delta = custm_time - time_window;
		if(delta > 0 && delta < smal_delta) {
			smal_delta = delta;
			*Y = temp_y;
			*F = i;
		}
	}
}

/**
 * Takes a "human readable" time window value, figures out the best Y and F values to represent
 * it as a bit field, stores the bit field as both the upper and lower time window fields of the MSR
 */
void
set_pkg_time_window_limit(int fd, uint64_t addr, double custm_time) {
	uint64_t msr;
	uint64_t Y;
	uint64_t F;
	msr = read_msr(fd, addr);
	//Set the customized time window.
	convert_optimal_yf_from_time(&Y, &F, custm_time);

	//Keep everything else the same.
	//#1 time window bits
	putBitField(F, &msr, F_SIZE, F_START_TIMEWINDOW_1);
	putBitField(Y, &msr, Y_SIZE, Y_START_TIMEWINDOW_1);
	//#2 time window bits
	putBitField(F, &msr, F_SIZE, F_START_TIMEWINDOW_2);
	putBitField(Y, &msr, Y_SIZE, Y_START_TIMEWINDOW_2);

	write_msr(fd, addr, msr);
}

/**
 * Takes a "human readable" time window value, figures out the best Y and F values to represent
 * it as a bit field, stores the bit field as just the lower time window
 */
void
set_dram_time_window_limit(int fd, uint64_t addr, double custm_time) {
	uint64_t msr;
	uint64_t Y;
	uint64_t F;
	msr = read_msr(fd, addr);
	//Set the customized time window.
	convert_optimal_yf_from_time(&Y, &F, custm_time);

	//Keep everything else the same.
	//#1 time window bits
	putBitField(F, &msr, F_SIZE, F_START_TIMEWINDOW_1);
	putBitField(Y, &msr, Y_SIZE, Y_START_TIMEWINDOW_1);

	write_msr(fd, addr, msr);
}

/**
 * Takes a "human readable" power limit value, converts it into a storable bit field by dividing it by the rapl_unit's power attrib, stores it in both
 * the package power limit sections of the MSR
 * (***is rapl_unit just a conversion unit for each type of data?)
 */
void
set_pkg_power_limit(int fd, uint64_t addr, double custm_power) {
	uint64_t msr;
	msr = read_msr(fd, addr);
	//Set the customized power.
	uint64_t power_limit = custm_power / rapl_unit.power;
	//Keep everything else the same.
	putBitField(power_limit, &msr, POWER_LIMIT_FIELD_SIZE, POWER_LIMIT_START_LOW_END);
	putBitField(power_limit, &msr, POWER_LIMIT_FIELD_SIZE, POWER_LIMIT_START_HIGH_END);

	write_msr(fd, addr, msr);
}

/**
 * Takes "human readable" power value, adjusts it to fit in the bit field, stores in the low end power limit field
 * (the high end is also included here but commented out)
 */
void
set_dram_power_limit(int fd, uint64_t addr, double custm_power) {
	uint64_t msr;
	msr = read_msr(fd, addr);
	//Set the customized power.
	uint64_t power_limit = custm_power / rapl_unit.power;
	//Keep everything else the same.
	putBitField(power_limit, &msr, POWER_LIMIT_FIELD_SIZE, POWER_LIMIT_START_LOW_END);
//	putBitField(power_limit, &msr, POWER_LIMIT_FIELD_SIZE, POWER_LIMIT_START_HIGH_END);

	write_msr(fd, addr, msr);

}

/* Gets conversion unit from msr data->meaningful data of a certain unit */
void get_msr_unit(rapl_msr_unit *unit_obj, uint64_t data) {

	uint64_t power_bits = extractBitField(data, POWER_BIT_SIZE, POWER_BIT_START);
	uint64_t energy_bits = extractBitField(data, ENERGY_BIT_SIZE, ENERGY_BIT_START);
	uint64_t time_bits = extractBitField(data, TIME_BIT_SIZE, TIME_BIT_START);       //// ask kenan - what is the time being retrieved?

	unit_obj->power  = (1.0 / _2POW(power_bits) );
	unit_obj->energy = (1.0 / _2POW(energy_bits));
	unit_obj->time   = (1.0 / _2POW(time_bits)  );
}

/**
 * Calculates highest possible energy value the MSR can represent, by multiplying the highest possible
 * value for a 32 bit register by the energy conversion unit. This is used when calculating energy difference
 * across an instance of the register wrapping around.
 */
double
get_rapl_wraparound(double energy_unit) {
	uint32_t highest_possible_register_value = 0xFFFFFFFF;
	return highest_possible_register_value * energy_unit;
}

/**
 * Gets rapl parameters (see two functions below) but this one is used to get pkg information. Sneaks MSR_PKG_POWER_INFO in
 * so the caller doesnt need to know that value
 */
void
get_rapl_pkg_parameters(int fd, rapl_msr_unit *unit_obj, rapl_msr_parameter *paras) {
	get_rapl_parameters(fd, MSR_PKG_POWER_INFO, (rapl_msr_unit *)unit_obj, (rapl_msr_parameter *)paras);
}

/**
 * Gets rapl parameters (see function below) but used to get dram information. Sneaks MSR_PKG_POWER_INFO in
 * so the caller doesnt need to know that value
 */
void
get_rapl_dram_parameters(int fd, rapl_msr_unit *unit_obj, rapl_msr_parameter *paras) {
	get_rapl_parameters(fd, MSR_DRAM_POWER_INFO, (rapl_msr_unit *)unit_obj, (rapl_msr_parameter *)paras);
}

/**
 * Extracts bit fields for thermal spec power, max power, min power, and max time window from MSR.
 * Processes bit field data into relevant human-readable number using the unit object's data
 * ...data put into unit_obj was proceessed with fomula [1.0 / _2POW(data)]
 */
void
get_rapl_parameters(int fd, uint64_t msr_addr, rapl_msr_unit *unit_obj, rapl_msr_parameter *paras) {
	uint64_t thermal_spec_power;
	uint64_t max_power;
	uint64_t min_power;
	uint64_t max_time_window;
	uint64_t power_info;

	power_info = read_msr(fd, msr_addr);

	thermal_spec_power = extractBitField(power_info, POWER_INFO_FIELD_SIZE, THERMAL_SPEC_POWER_START);
	min_power = extractBitField(power_info, POWER_INFO_FIELD_SIZE, MIN_POWER_START);
	max_power = extractBitField(power_info, POWER_INFO_FIELD_SIZE, MAX_POWER_START);
	max_time_window = extractBitField(power_info, MAX_TIME_WINDOW_SIZE, MAX_TIME_WINDOW_START);


	paras->thermal_spec_power = unit_obj->power * thermal_spec_power;
	paras->min_power = unit_obj->power * min_power;
	paras->max_power = unit_obj->power * max_power;
	paras->max_time_window = unit_obj->time * max_time_window;
}

/** Fill an array of 4 places with the 4 fields of rapl_msr_parameter. Takes an array
 * of those, indexed by domain, and selects the 4 fields from the array entry of that
 * domain.
 */
void
getPowerSpec(double result[4], rapl_msr_parameter *parameters, int domain) {
	result[0] = parameters[domain].thermal_spec_power;
	result[1] = parameters[domain].min_power;
	result[2] = parameters[domain].max_power;
	result[3] = parameters[domain].max_time_window;
}
