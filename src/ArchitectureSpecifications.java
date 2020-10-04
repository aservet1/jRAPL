package jrapl;

public final class ArchitectureSpecifications {

	private boolean readingDRAM;
	private boolean readingGPU;
	private int NUM_SOCKETS;	
	private int ENERGY_WRAP_AROUND;

	private int numberOfPowerDomainsSupported;

	public native static int GetWrapAroundEnergy();
	public native static int GetSocketNum();
	public native static int DramOrGpu();


}
