package jrapl;

public final class ArchitectureSpecifications {

	public static final boolean readingDRAM;
	public static final boolean readingGPU;
	public static final int NUM_SOCKETS;	
	public static final int ENERGY_WRAP_AROUND;

	//public static final int numberOfPowerDomainsSupported;

	public native static int GetWraparoundEnergy();
	public native static int GetSocketNum();
	public native static int DramOrGpu();


	static {
		// base bwlo 2 off of output or DramOrGpu()
		readingDRAM = false;
		readingGPU = false;
		NUM_SOCKETS = GetSocketNum();
		ENERGY_WRAP_AROUND = GetWraparoundEnergy();
	}

}
