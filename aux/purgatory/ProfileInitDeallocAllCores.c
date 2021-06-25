
// just for test purposes, not part of real RAPL implementation
void
ProfileInitAllCores(int num_readings) {
	int i;
	char msr_filename[BUFSIZ];

	num_sockets = getSocketNum(); 
	micro_architecture = get_micro_architecture();
	power_domains_supported = get_power_domains_supported(micro_architecture);
	uint64_t num_pkg_thread = get_num_pkg_thread();

	/*only two domains are supported for parameters check*/
	parameters = (rapl_msr_parameter *)malloc(2 * sizeof(rapl_msr_parameter));
	num_cores = num_sockets*num_pkg_thread;
	msr_fds = (int *) malloc(num_sockets * sizeof(int) * num_cores);
	
	for(i = 0; i < num_cores; i++) {
		sprintf(msr_filename, "/dev/cpu/%d/msr", i);
		printf("%s\n",msr_filename);
		msr_fds[i] = open(msr_filename, O_RDWR);
	}

	rapl_unit = get_rapl_unit(msr_fds[0]);
	wraparound_energy = get_wraparound_energy(rapl_unit.energy);
	for(int _ = 0; _ < num_readings; _++) {
		double pkg[num_cores];
		double dram[num_cores];
		double core[num_cores];
		double gpu[num_cores];
		for(int i = 0; i < num_cores; i++) {
			pkg[i]  =  read_pkg(i);
			dram[i] =  read_dram(i);
			core[i] =  read_core(i);
			gpu[i]  =  read_gpu(i);
		}
		for (int c = 0; c < num_cores; c++) {
			printf("%d || pkg: %f dram: %f gpu: %f core: %f\n", c, pkg[c], dram[c], gpu[c], core[c]);
		} printf("----------------------\n");
		sleep(1);
	}
}

// just for test purposes, not part of real RAPL implementation
void
ProfileDeallocAllCores() {
	for (int i = 0; i < num_cores; i++) {
		close(msr_fds[i]);
	} free(msr_fds); msr_fds = NULL;
	free(parameters); parameters = NULL;
}
