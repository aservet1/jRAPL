package jrapl;

public final class ArchitectureSpecifications {

	public static final boolean readingDRAM;
	public static final boolean readingGPU;
	public static final int NUM_SOCKETS;	
	public static final int ENERGY_WRAP_AROUND;
	public static final int cpuModel;
	public static final String cpuModelName;


	public native static int DramOrGpu();
	public native static int GetSocketNum();
	public native static int GetWraparoundEnergy();
	public native static String GetCpuModelName();
	public native static int GetCpuModel();


	static {
		// base bwlo 2 off of output or DramOrGpu()
		readingDRAM = false;
		readingGPU = false;
		NUM_SOCKETS = GetSocketNum();
		ENERGY_WRAP_AROUND = GetWraparoundEnergy();
	}


}
