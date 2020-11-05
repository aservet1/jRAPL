package jrapl;

public final class ArchSpec {

	public static final boolean readingDRAM;
	public static final boolean readingGPU;
	public static final int NUM_SOCKETS;	
	public static final int ENERGY_WRAP_AROUND;
	public static final int cpuModel;
	public static final String cpuModelName;


	public native static int PowerDomainsSupported();
	public native static int GetSocketNum();
	public native static int getWraparoundEnergy();
	public native static String GetCpuModelName();
	public native static int GetCpuModel();

	// which power domains are supported
	public native static boolean dramSupported();
	public native static boolean gpuSupported();
	public native static boolean cpuSupported();
	public native static boolean pkgSupported();


	public native static String EnergyStatsStringFormat();

	static {

		// @TODO this is just a temporary solution to get the library loaded while testing out this
		// stuff directly by calling 'java jrapl.ArchSpec' to run main(), honestly these utilities
		// won't ever be used outside of an EnergyManger being inited and dealloc'd
		new EnergyManager().init();

		cpuModel = GetCpuModel();
		cpuModelName = GetCpuModelName();

		int rd = PowerDomainsSupported();
		if ( rd == 3 ) {
			readingDRAM = true;
			readingGPU  = true;
		} else if ( rd == 1 ) {
			readingDRAM = true;
			readingGPU  = false;
		} else if ( rd == 2 ) {
			readingDRAM = false;
			readingGPU  = true;
		} else {
			readingDRAM = false;
			readingGPU  = false;
		}

		NUM_SOCKETS = GetSocketNum();
		ENERGY_WRAP_AROUND = getWraparoundEnergy();
	}

	public static void init() {} // do-nothing function to trigger the static block...probably a better way of doing this

	public static String infoString() {
		String s = new String();
		s += "readingDRAM: " + readingDRAM + "\n";
		s += "readingGPU: " + readingGPU + "\n";
		s += "NUM_SOCKETS: " + NUM_SOCKETS + "\n";
		s += "ENERGY_WRAP_AROUND: " + ENERGY_WRAP_AROUND + "\n";
		s += "cpuModel: " + Integer.toHexString(cpuModel) + "\n";
		s += "cpuModelName: " + cpuModelName;
		return s;
	}

	public static void main(String[] args) {
		EnergyManager manager = new EnergyManager();
		manager.init();
		System.out.println(EnergyStatsStringFormat());
		manager.dealloc();
	}

}




