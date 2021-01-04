package jrapl;

public final class ArchSpec {

	//public static final boolean readingDRAM; @TODO -- is this obsolete??
	//public static final boolean readingGPU; @TODO -- is this obsolete??
	public static final int NUM_SOCKETS;	
	public static final int NUM_STATS_PER_SOCKET;
	public static final int RAPL_WRAPAROUND;
	public static final int CPU_MODEL;
	public static final String CPU_MODEL_NAME;
	public static final String ENERGY_STATS_STRING_FORMAT;

	//public native static int powerDomainsSupported(); @TODO -- is this obsolete??
	public native static int getSocketNum();
	public native static int getWraparoundEnergy();
	public native static String getCpuModelName();
	public native static int getCpuModel();
	public native static String energyStatsStringFormat();

	// which power domains are supported @TODO -- are these obsolete??
	//public native static boolean dramSupported();
	//public native static boolean gpuSupported();
	//public native static boolean coreSupported();
	//public native static boolean pkgSupported();

	// the indexes of where power domains are in the returned array of energy stats
	// I wonder if this is the best class to calculate and store these indices.
	//  it's definitely safe and ok enough for now and possibly permanently.
	//  but just keep it in the back of your mind that you might wanna relocate
	//  to something more specific, maybe classes only relevant to energy monitoring,
	//  since where else would you even use these values?
	public static final int DRAM_ARRAY_INDEX;
	public static final int GPU_ARRAY_INDEX;
	public static final int CORE_ARRAY_INDEX;
	public static final int PKG_ARRAY_INDEX;
	//TODO -- there's a 5th possible power domain, right? like full motherboard energy or something

	static {

		// @TODO this is just a temporary solution to get the library loaded while testing out this
		// stuff directly by calling 'java jrapl.ArchSpec' to run main(), honestly these utilities
		// won't ever be used outside of an EnergyManger being inited and dealloc'd
		new EnergyManager().init();

		CPU_MODEL = getCpuModel();
		CPU_MODEL_NAME = getCpuModelName();

		//int rd = powerDomainsSupported();
		//if ( rd == 3 ) {
		////	readingDRAM = true;
		////	readingGPU  = true;
		//} else if ( rd == 1 ) {
		////	readingDRAM = true;
		////	readingGPU  = false;
		//} else if ( rd == 2 ) {
		////	readingDRAM = false;
		////	readingGPU  = true;
		//} else {
		////	readingDRAM = false;
		////	readingGPU  = false;
		//}

		NUM_SOCKETS = getSocketNum();
		RAPL_WRAPAROUND = getWraparoundEnergy();

		ENERGY_STATS_STRING_FORMAT = energyStatsStringFormat();
		NUM_STATS_PER_SOCKET = ENERGY_STATS_STRING_FORMAT.split("@")[0].split(",").length;

		int dramIndex = -1, gpuIndex = -1, coreIndex = -1, pkgIndex = -1;
		String domains_string = ENERGY_STATS_STRING_FORMAT.split("@")[0];
		String[] positions = domains_string.split(",");
		for ( int i = 0; i < positions.length; i++ ) {
			switch (positions[i]) {
				case "dram":
					dramIndex = i;
					break;
				case "gpu":
					gpuIndex = i;
					break;
				case "core":
					coreIndex = i;
					break;
				case "pkg":
					pkgIndex = i;
					break;
				default:
					System.err.println("unexpected format string: " + domains_string);
					System.exit(0);
			}	
		}
		DRAM_ARRAY_INDEX = dramIndex;
		GPU_ARRAY_INDEX = gpuIndex;
		CORE_ARRAY_INDEX = coreIndex;
		PKG_ARRAY_INDEX = pkgIndex;
	}
	

	public static void init() {} // do-nothing function to trigger the static block...probably a better way of doing this

	public static String infoString() {
		String s = new String();
		//s += "readingDRAM: " + readingDRAM + "\n";
		//s += "readingGPU: " + readingGPU + "\n";
		s += "NUM_SOCKETS: " + NUM_SOCKETS + "\n";
		s += "RAPL_WRAPAROUND: " + RAPL_WRAPAROUND + "\n";
		s += "CPU_MODEL: " + Integer.toHexString(CPU_MODEL) + "\n";
		s += "CPU_MODEL_NAME: " + CPU_MODEL_NAME;
		s += "ENERGY_STATS_STRING_FORMAT: " + ENERGY_STATS_STRING_FORMAT;
		s += "DRAM_ARRAY_INDEX: " + DRAM_ARRAY_INDEX + "\n";
		s += "GPU_ARRAY_INDEX: " + GPU_ARRAY_INDEX + "\n";
		s += "CORE_ARRAY_INDEX: " + CORE_ARRAY_INDEX + "\n";
		s += "PKG_ARRAY_INDEX: " + PKG_ARRAY_INDEX + "\n";
		return s;
	}

}




