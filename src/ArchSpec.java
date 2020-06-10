package jrapl;

/**
*	Relevant functions for geting Architecture Specifications from the Java end.
*/
public class ArchSpec extends JRAPL {

	/** Reports number of CPU sockets for the current system
	 *  @return number of CPU sockets
	*/
	public native static int GetSocketNum();

	/** Tells if the first reading per socket in EnergyStatCheck is DRAM energy or GPU energy
	 *  @return 0 for undefined architecture, 1 for DRAM, 2 for GPU
	*/
	public native static int DramOrGpu();


}
