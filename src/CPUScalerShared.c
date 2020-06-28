
#include "CPUScalerShared.h"

/** <Alejandro's Interpretation>
 *  Takes the energy info and packages it into a formatted string... # delimits the 3 energy attribs and @ delimits multiple package readings (1 pkg = 3 energy attribs)
 *	Sets offset to the end of the string by the end of this
 *	I belive the i is an iterator to see how many packages have been put into the string
 *	If more than 1 pkg, puts a @ at the end of the string because there's going to be another set of package info after that
 */
void copy_to_string(char *ener_info, char uncore_buffer[60], int uncore_num, char cpu_buffer[60], int cpu_num, char package_buffer[60], int package_num, int i, int *offset)
{
	memcpy(ener_info + *offset, &uncore_buffer, uncore_num);
	ener_info[*offset + uncore_num] = '#';
	memcpy(ener_info + *offset + uncore_num + 1, &cpu_buffer, cpu_num);
	ener_info[*offset + uncore_num + cpu_num + 1] = '#';
	if(i < num_pkg - 1) {
		memcpy(ener_info + *offset + uncore_num + cpu_num + 2, &package_buffer, package_num);
		*offset += uncore_num + cpu_num + package_num + 2;
		if(num_pkg > 1) {
			ener_info[*offset++] = '@';
		}
	} else {
		memcpy(ener_info + *offset + uncore_num + cpu_num + 2, &package_buffer, package_num + 1);
	}
}

rapl_msr_unit get_rapl_unit()
{
	rapl_msr_unit rapl_unit;
	uint64_t unit_info = read_msr(fd[0], MSR_RAPL_POWER_UNIT);
	get_msr_unit(&rapl_unit, unit_info);
	return rapl_unit;
}


/** <Alejandro's Interpretation>
 *  In short, fills up the energy info buffers appropriately.
 *	Pass in gpu, dram, cpu, and package buffers. There are num_pkg buffers per type of computer thing. One buffer per package that the computer has.
 *	Filling up the buffers. The for loop is so you get a different reading for all the packages. Reads the msr for that package with fd[i]
 *  Based on the CPU model, it either adds dram info or gpu info. I guess that certain models use gpus and others drams?
 *  Interpret/process MSR reading for dram differently based on CPU model before storing it in the buffer.......................
 */
void initialize_energy_info(char gpu_buffer[num_pkg][60], char dram_buffer[num_pkg][60], char cpu_buffer[num_pkg][60], char package_buffer[num_pkg][60])
{
	uint32_t cpu_model = get_cpu_model();
	double package[num_pkg];
	double pp0[num_pkg];
	double pp1[num_pkg];
	double dram[num_pkg];
	double result = 0.0;
	int info_size = 0;
	int i = 0;
	rapl_msr_unit rapl_unit = get_rapl_unit();
	for (; i < num_pkg; i++) {

		if (timingMsrReadings) gettimeofday(&start, NULL);
		result = read_msr(fd[i], MSR_PKG_ENERGY_STATUS);	//First 32 bits so don't need shift bits.
		package[i] = (double) result * rapl_unit.energy;
		if (timingMsrReadings) {
			gettimeofday(&end,NULL);
			timeval_subtract(&diff, &end, &start);
			logTime("PACKAGE Socket 0", diff.tv_sec*1000 + diff.tv_usec); //@TODO it should be "Socket (i)" but I didn't want to deal with string building in C and our copmuters only have 1 socket, but this definitely needs to be changed to scale up to 2 socket machines (jRAPL currently only works for 2 socket machines)
		}

		if (timingMsrReadings)gettimeofday(&start, NULL);
		result = read_msr(fd[i], MSR_PP0_ENERGY_STATUS);
		pp0[i] = (double) result * rapl_unit.energy;
		if (timingMsrReadings) {
			gettimeofday(&end,NULL);
			timeval_subtract(&diff, &end, &start);
			logTime("CORE Socket 0", diff.tv_sec*1000 + diff.tv_usec); //@TODO it should be "Socket (i)" but I didn't want to deal with string building in C and our copmuters only have 1 socket, but this definitely needs to be changed to scale up to 2 socket machines (jRAPL currently only works for 2 socket machines)
		}

		sprintf(package_buffer[i], "%f", package[i]);
		sprintf(cpu_buffer[i], "%f", pp0[i]);
		int architecture_category = get_architecture_category(cpu_model);
		switch(architecture_category) {
			case READ_FROM_DRAM:
				if (timingMsrReadings) gettimeofday(&start, NULL);
				result = read_msr(fd[i],MSR_DRAM_ENERGY_STATUS);
				if (timingMsrReadings) {
					gettimeofday(&end,NULL);
					timeval_subtract(&diff, &end, &start);
					logTime("DRAM Socket 0", diff.tv_sec*1000 + diff.tv_usec); //@TODO it should be "Socket (i)" but I didn't want to deal with string building in C and our copmuters only have 1 socket, but this definitely needs to be changed to scale up to 2 socket machines (jRAPL currently only works for 2 socket machines)
				}
				if (cpu_model == BROADWELL || cpu_model == BROADWELL2) {
					dram[i] =(double)result*MSR_DRAM_ENERGY_UNIT;
				} else {
					dram[i] =(double)result*rapl_unit.energy;
				}

				sprintf(dram_buffer[i], "%f", dram[i]);

				info_size += strlen(package_buffer[i]) + strlen(dram_buffer[i]) + strlen(cpu_buffer[i]) + 4;

				/*Insert socket number*/

				break;
			case READ_FROM_GPU:
				if (timingMsrReadings) gettimeofday(&start, NULL);
				result = read_msr(fd[i],MSR_PP1_ENERGY_STATUS);
				if (timingMsrReadings) {
					gettimeofday(&end,NULL);
					timeval_subtract(&diff, &end, &start);
					logTime("GPU Socket 0", diff.tv_sec*1000 + diff.tv_usec); //@TODO it should be "Socket (i)" but I didn't want to deal with string building in C and our copmuters only have 1 socket, but this definitely needs to be changed to scale up to 2 socket machines (jRAPL currently only works for 2 socket machines)
				}
				pp1[i] = (double) result *rapl_unit.energy;

				sprintf(gpu_buffer[i], "%f", pp1[i]);

				info_size += strlen(package_buffer[i]) + strlen(gpu_buffer[i]) + strlen(cpu_buffer[i]) + 4;
				break;
			case UNDEFINED_ARCHITECTURE:
				printf("Architecture not found\n");
				break;

		}
	}

}


char* EnergyStatCheck_C(char *ener_info){
    char gpu_buffer[num_pkg][60];
	char dram_buffer[num_pkg][60];
	char cpu_buffer[num_pkg][60];
	char package_buffer[num_pkg][60];
	int dram_num = 0L;	///  dram_num is the id number of that component
	int cpu_num = 0L;	///  same applies to the other x_num varaibles (num is id number)
	uint32_t cpu_model = get_cpu_model();

	int package_num = 0L;
	int gpu_num = 0L;
	//construct a string
    ener_info[511] = '\0';
	int i;
	int offset = 0;


  	bzero(ener_info, 512);
	initialize_energy_info(gpu_buffer, dram_buffer, cpu_buffer, package_buffer);
	int architecture_catergory = get_architecture_category(cpu_model);
	for(i = 0; i < num_pkg; i++) {
		switch(architecture_catergory) {
			case READ_FROM_DRAM:

				/*Insert socket number*/
				dram_num = strlen(dram_buffer[i]);
				cpu_num = strlen(cpu_buffer[i]);
				package_num = strlen(package_buffer[i]);

				//copy_to_string(ener_info, dram_buffer, dram_num, cpu_buffer, cpu_num, package_buffer, package_num, i, &offset);
				memcpy(ener_info + offset, &dram_buffer[i], dram_num);
				//split sign
				ener_info[offset + dram_num] = '#';
				memcpy(ener_info + offset + dram_num + 1, &cpu_buffer[i], cpu_num);
				ener_info[offset + dram_num + cpu_num + 1] = '#';
				if(i < num_pkg - 1) {
					memcpy(ener_info + offset + dram_num + cpu_num + 2, &package_buffer[i], package_num);
					offset += dram_num + cpu_num + package_num + 2;
					if(num_pkg > 1) {
						ener_info[offset] = '@';
						offset++;
					}
				} else {
					memcpy(ener_info + offset + dram_num + cpu_num + 2, &package_buffer[i], package_num + 1);
				}

				break;
			case READ_FROM_GPU:

				gpu_num = strlen(gpu_buffer[i]);
				cpu_num = strlen(cpu_buffer[i]);
				package_num = strlen(package_buffer[i]);

				//copy_to_string(ener_info, gpu_buffer, gpu_num, cpu_buffer, cpu_num, package_buffer, package_num, i, &offset);
				memcpy(ener_info + offset, &gpu_buffer[i], gpu_num);
				//split sign
				ener_info[offset + gpu_num] = '#';
				memcpy(ener_info + offset + gpu_num + 1, &cpu_buffer[i], cpu_num);
				ener_info[offset + gpu_num + cpu_num + 1] = '#';
				if(i < num_pkg - 1) {
					memcpy(ener_info + offset + gpu_num + cpu_num + 2, &package_buffer[i], package_num);
					offset += gpu_num + cpu_num + package_num + 2;
					if(num_pkg > 1) {
						ener_info[offset] = '@';
						offset++;
					}
				} else {
					memcpy(ener_info + offset + gpu_num + cpu_num + 2, &package_buffer[i],
							package_num + 1);
				}

				break;
		case UNDEFINED_ARCHITECTURE:
				printf("Architecture not found\n");
				break;

		}
	}
    return ener_info;

}
