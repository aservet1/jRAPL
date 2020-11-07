package jrapl;

public final class ArchSpec {

	//public static final boolean readingDRAM;
	//public static final boolean readingGPU;
	public static final int NUM_SOCKETS;	
	public static final int NUM_STATS_PER_SOCKET;
	public static final int RAPL_WRAPAROUND;
	public static final int CPU_MODEL;
	public static final String CPU_MODEL_NAME;
	public static final String ENERGY_STATS_STRING_FORMAT;

	public native static int PowerDomainsSupported();
	public native static int GetSocketNum();
	public native static int getWraparoundEnergy();
	public native static String GetCpuModelName();
	public native static int GetCpuModel();
	public native static String EnergyStatsStringFormat();

	// which power domains are supported
	public native static boolean dramSupported();
	public native static boolean gpuSupported();
	public native static boolean cpuSupported();
	public native static boolean pkgSupported();

	static {

		// @TODO this is just a temporary solution to get the library loaded while testing out this
		// stuff directly by calling 'java jrapl.ArchSpec' to run main(), honestly these utilities
		// won't ever be used outside of an EnergyManger being inited and dealloc'd
		new EnergyManager().init();

		CPU_MODEL = GetCpuModel();
		CPU_MODEL_NAME = GetCpuModelName();

		int rd = PowerDomainsSupported();
		if ( rd == 3 ) {
		//	readingDRAM = true;
		//	readingGPU  = true;
		} else if ( rd == 1 ) {
		//	readingDRAM = true;
		//	readingGPU  = false;
		} else if ( rd == 2 ) {
		//	readingDRAM = false;
		//	readingGPU  = true;
		} else {
		//	readingDRAM = false;
		//	readingGPU  = false;
		}

		NUM_SOCKETS = GetSocketNum();
		RAPL_WRAPAROUND = getWraparoundEnergy();

		ENERGY_STATS_STRING_FORMAT = EnergyStatsStringFormat();
		NUM_STATS_PER_SOCKET = ENERGY_STATS_STRING_FORMAT.split("@")[0].split(",").length;
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
		return s;
	}

	public static void main(String[] args) {
		EnergyManager manager = new EnergyManager();
		manager.init();
		System.out.println(EnergyStatsStringFormat());
		System.out.println(NUM_STATS_PER_SOCKET);
		manager.dealloc();
	}

}




