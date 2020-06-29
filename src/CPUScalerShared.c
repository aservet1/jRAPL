
#include "CPUScalerShared.h"


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

}
