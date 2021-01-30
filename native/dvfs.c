#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//// DVFS stands for Dynamic Voltage and Frequency Scaling
//// Not currently used in current working project, but will be the native function basis for DvfsEnergyController.java once that is implemented

/** Return 1 if negative, otherwise return number of base 10 digits in value
 *	i.e. 147 => 3, 328974 => 6, -1812973128973 => 1, etc
 */
int get_pos_intnum(int value) {
	int num = 1;
	while(value > 9) {
		num++;
		value /= 10;
	}
	return num;
}

/** Sets the DVFS governor policy
 *	Returns 1 if failed writing opening and closing file. Return 0 if no errors.
 */
int check_write_gov(int cores, char govFile[cores][60], const char *target) {
	int i;
	int rc;
	FILE *f[cores];
	size_t data_length, data_written;
	char string[cores][25];

	for (i = 0; i < cores; i++) {
		f[i] = fopen(govFile[i], "r");
		if (f[i] == NULL) {
			//LOGI("Failed to open %s: %s", filename, strerror(errno));
			printf("Failed to open %s", govFile[i]);
			return 1;
		}

		fscanf(f[i], "%s", string[i]);

		rc = fclose(f[i]);
		if (rc != 0) {
			//LOGI("Failed to close %s: %s", filename, strerror(rc));
			printf("Failed to close %s", govFile[i]);
			return 1;
		}

		if (strcmp(string[i], target) != 0) {
			//Write govenor
			f[i] = fopen(govFile[i], "w");
			if (f[i] == NULL) {
				//LOGI("Failed to open %s: %s", govFile, strerror(errno));
				printf("Failed to open %s", govFile[i]);
				return 1;
			}

			data_length = strlen(target);
			data_written = fwrite(target, 1, data_length, f[i]);
			if (data_length != data_written) {
				//LOGI("Failed to write to %s: %s", filename, strerror(errno));
				printf("Failed to write %s", target);
				return 1;
			}

			rc = fclose(f[i]);
			if (rc != 0) {
				//LOGI("Failed to close %s: %s", filename, strerror(rc));
				printf("Failed to close %s", govFile[i]);
				return 1;
			}
		}
	}
	return 0;
}

/** Sets the desired CPU frequency for all CPU cores
 *	Returns 1 if failed writing opening and closing file. Return 0 if no errors.
 */
int write_freq_all_cores(int cores, char filename[cores][60], const char *cur_freq, const char *scal_freq, int freq) {
	int i;
	FILE *f[cores];
	int rc;
	size_t data_length, data_written;
	int cpu_freq[cores];
	int scal_cpufreq[cores];

	for(i = 0; i < cores; i++) {
		f[i] = fopen(filename[i], "w");
		if (f[i] == NULL) {
			//LOGI("Failed to open %s: %s", filename, strerror(errno));
			printf("Failed to open %s", filename[i]);
			return 1;
		}

		data_length = get_pos_intnum(freq);
		data_written = fprintf(f[i], "%d", freq);  //For integer

		if (data_length != data_written) {
			//LOGI("Failed to write to %s: %s", filename, strerror(errno));
			printf("Failed to write %s", filename[i]);
			return 1;
		}

		rc = fclose(f[i]);
		if (rc != 0) {
			//LOGI("Failed to close %s: %s", filename, strerror(rc));
			printf("Failed to close %s", filename[i]);
			return 1;
		}
		f[i] = fopen(cur_freq, "r");
		if (f[i] == NULL) {
			//LOGI("Failed to open %s: %s", filename, strerror(errno));
			printf("Failed to open %s", cur_freq);
			return 1;
		}

		fscanf(f[i], "%d", &cpu_freq[i]);
		rc= fclose(f[i]);
		f[i] = fopen(scal_freq, "r");
		if (f[i] == NULL) {
			//LOGI("Failed to open %s: %s", filename, strerror(errno));
			printf("Failed to open %s", scal_freq);
			return 1;
		}
		fscanf(f[i], "%d", &scal_cpufreq[i]);
		rc= fclose(f[i]);
		
		printf("cpu_freq: %d\n", cpu_freq[i]);
		printf("scal_cpufreq: %d\n", scal_cpufreq[i]);
	}
	return 0;
}
