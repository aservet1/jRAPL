package jrapl;

/**
*	Access to architecture specifications. Could probably get more functions from the C side over here...
*/
public class ArchSpec extends JRAPL {

	private ArchSpec() {} // private constructor -- never initialized

	/** Reports number of CPU sockets for the current system
	 *  @return number of CPU sockets
	*/
	public native static int GetSocketNum();

	/** Tells if the first reading per socket in EnergyStatCheck is DRAM energy or GPU energy
	 *  @return 0 for undefined architecture, 1 for DRAM, 2 for GPU
	*/
	public native static int DramOrGpu();


}
