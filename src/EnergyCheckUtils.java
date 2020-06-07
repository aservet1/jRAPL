package jrapl;

public class EnergyCheckUtils extends JRAPL {
	/** Documentation not done. See CPUScaler.c for source
	 *  Initializes the energy profile of the system. To be called before accessing any jRAPL utility.
	 *  Information initialized (stored entirely in static global variables on the C side):
	 *  	CPU Model
	 *  	Number of CPU sockets
	 *  	Array of file handles for MSR readings
	 *
	 *  @return wraparoundValue -- 
	 *
	*/
	public native static int ProfileInit();

	/** Gets the number of CPU sockets the system has
	 *  @return number of CPU sockets
	*/
	public native static int GetSocketNum();

	/** Tells if the first reading per socket in EnergyStatCheck is DRAM energy or GPU energy
	 *  @return 0 for undefined architecture, 1 for DRAM, 2 for GPU
	*/
	public native static int DramOrGpu();

	/** Returns a string with current total energy consumption reported in MSR registers.
	 *	Formatted " 1stSocketInfo @ 2ndSocketInfo @ ... @ NthSocketInfo " with @ delimiters
	 *	Each NthSocketInfo subsection formatted " dram_energy # cpu_energy # package_energy " with # delimieters
	 *	This string gets parsed into an array in getEnergyStats().
	 *	@return String containing per socket energy info
	*/
	public native static String EnergyStatCheck();

	/** Free all memory allocated in ProfileInit()
	*/
	public native static void ProfileDealloc();

	public static int wrapAroundValue;
	public static int socketNum;

	/**
	 * Parses string generated from the native EnergyStatCheck() method into an array of doubles.
	 * @return an array of current energy information.
	 * Array will be size (3 * socketnum). There will be three entries per socket
	 * The first entry is: Dram/uncore gpu energy (depends on the cpu architecture)
	 * The second entry is: CPU energy
	 * The third entry is: Package energy
	 * General layout of the array:
	 * 	[dram_s1, cpu_s1, pkg_s1, dram_s2, cpu_s2, pkg_s2, ... , dram_sn, cpu_sn, pkg_sn]
	 *	sn means socket number associated with this reading for all n greater than 1 	//@TODO -- is socket numbering 0 indexed or 1 indexed?
	*/
	public static double[] getEnergyStats() {
		socketNum = GetSocketNum(); //@TODO -- is this redundant? can we just assume that it was already set during the sstatic block?
		String EnergyInfo = EnergyStatCheck();
		/*One Socket*/
		if(socketNum == 1) {
			double[] stats = new double[3]; // 3 stats per socket
			String[] energy = EnergyInfo.split("#");

			stats[0] = Double.parseDouble(energy[0]);
			stats[1] = Double.parseDouble(energy[1]);
			stats[2] = Double.parseDouble(energy[2]);

			return stats;

		} else {
		/*Multiple sockets*/
			String[] perSockEner = EnergyInfo.split("@");
			double[] stats = new double[3*socketNum]; // 3 stats per socket
			int count = 0;


			for(int i = 0; i < perSockEner.length; i++) {
				String[] energy = perSockEner[i].split("#");
				for(int j = 0; j < energy.length; j++) {
					count = i * 3 + j;	//accumulative count
					stats[count] = Double.parseDouble(energy[j]);
				}
			}
			return stats;
		}
	}

	/** Is there a point to this??? Frees memory allocated by ProfileInit().
	*/
	public static void DeallocProfile() {
		ProfileDealloc();
	}

	public static void main(String[] args)
	{
		ProfileInit();

		EnergyReadingCollector ec = new EnergyReadingCollector();

		ec.startReading();
		try { Thread.sleep(3000); } catch (Exception e) {}
		ec.stopReading();

		System.out.println(ec);

		ProfileDealloc();
	}

}
