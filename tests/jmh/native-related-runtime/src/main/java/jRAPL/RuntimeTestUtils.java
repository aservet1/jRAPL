package jRAPL;

import java.util.Arrays;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;

/** Access to utilities I set up for native C side timing */
public class RuntimeTestUtils
{
	static int DRAM  = 1, GPU = 2, CORE = 3, PKG = 4; // constants that align with macros defined on the C side

	/** Allocs relevant C side memory and sets up variables */
	public native static void initCSideTiming();
	/** Deallocs relevant C side memory */
	public native static void deallocCSideTiming();

	//Runs each function once and returns microsecond duration
	public native static long usecTimeProfileInit();
	public native static long usecTimeEnergyStatCheck();
	public native static long usecTimeProfileDealloc();
	public native static long[] usecTimeMSRRead(int powerDomain);

}
