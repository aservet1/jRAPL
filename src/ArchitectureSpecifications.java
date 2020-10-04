package jrapl;

public final class ArchitectureSpecifications {

	private boolean readingDRAM;
	private boolean readingGPU;
	private int NUM_SOCKETS;	
	private int ENERGY_WRAP_AROUND;

	private int numberOfPowerDomainsSupported;

	private native static int GetWrapAroundEnergy();
	private native static int GetSocketNum();
	private native static int DramOrGpu();


}
