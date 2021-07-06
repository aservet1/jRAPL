package jRAPL;

public class RuntimeTestUtils
{
	// constants that align with macros defined on the C side,
	// passed in to usecTimeMSRRead
	static final int DRAM = 1, GPU = 2, CORE = 3, PKG = 4;

	/** Allocs relevant C side memory and sets up variables */
	public native static void initCSideTiming();
	/** Deallocs relevant C side memory */
	public native static void deallocCSideTiming();

	//Runs the function in question once and returns usec runtime
	public native static long usecTimeProfileInit();
	public native static long usecTimeEnergyStatCheck();
	public native static long usecTimeProfileDealloc();
	public native static long[] usecTimeMSRRead(int powerDomain);

	public native static void ctimeStart();
	public native static void ctimeStop();
	public native static long ctimeElapsedUsec();

	public native static void energyStatCheckNoReturnValue();
	// public native static long usecTimeEnergyStatCheckNoBufferGuard();
	public native static long timechecker();
}