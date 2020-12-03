package jrapltesting;

import jrapl.*;

import java.util.Arrays;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;

/** <h1> DOCUMENTATION OUT OF DATE </h1>
*	Utilities for assessing runtime of (currently) a few things in C and Java. Can be used for performance diagnostics
*	on a particular system. These methods are accessed externally through a shell script that runs "java RuntimeTestUtils"
*	followed by command line arguments that tell the program which of these methods to use and how. Their output is then
*	picked back up and processed by the shell script that called it initially.
*/
public class RuntimeTestUtils
{
	/** Allocs relevant C side memory and sets up variables */
	public native static void initCSideTiming();
	/** Deallocs relevant C side memory */
	public native static void deallocCSideTiming();



	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Times a method call, returns time in microseconds
	*	@param method The equivalent of a function pointer in C/C++
	*	@return the time it took to run the method, in microseconds
	*/
	public static long timeMethod(Runnable method)
	{
		Instant start, end;
		start = Instant.now();
		method.run();
		end = Instant.now();
		long elapsed = Duration.between(start, end).toNanos() / 1000;
		return elapsed;
	}


	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Times a method multiple times. Stores runtime and then prints all
	*	readings to standard output, labelled by the method name.
	*/
	public static void timeMethodMultipleIterations(Runnable method, String name, int iterations)
	{
		int i = 0;
		long[] results = new long[iterations];
		for (int x = 0; x < iterations; x++) {
			if (name == "profileDealloc()") EnergyManager.profileInit(); // prevents a 'double free or corruption' error
			long time = timeMethod(method);
			results[i++] = time;
		}
		
		for (i = 0; i < results.length; i++)
			System.out.println(name + ": " + results[i]);
	}

	
	//Runs each function once and returns microseconds
	public native static long usecTimeProfileInit();
	public native static long usecTimeGetSocketNum();
	public native static long usecTimeEnergyStatCheck(int whichSocket);
	public native static long usecTimeProfileDealloc();
	public native static long[] usecTimeMSRRead(int powerDomain);


	// @TODO -- NOTE TO SELF: look into why the first MSR read from takes 5-10 long readings (84 ish)
	// some times and if that's something you can do anything about and if it matters
	public static void timeAllMSRReads(int iterations) {
		int DRAM  = 1, GPU = 2, CORE = 3, PKG = 4;
		long[][] dramTimes = new long[iterations][ArchSpec.NUM_SOCKETS];
		long[][] gpuTimes = new long[iterations][ArchSpec.NUM_SOCKETS];
		long[][] coreTimes = new long[iterations][ArchSpec.NUM_SOCKETS];
		long[][] pkgTimes = new long[iterations][ArchSpec.NUM_SOCKETS];
		
		//@TODO what to do if reading from a non power domain supported msr? like no gpu allowed? probably just return -1, right??
		int dramIndex = 0, gpuIndex = 0, coreIndex = 0, pkgIndex = 0;
		for (int n = 0; n < iterations; n++) coreTimes[coreIndex++] = usecTimeMSRRead(CORE);
		for (int n = 0; n < iterations; n++) gpuTimes[gpuIndex++] = usecTimeMSRRead(GPU);
		for (int n = 0; n < iterations; n++) dramTimes[dramIndex++] = usecTimeMSRRead(DRAM);
		for (int n = 0; n < iterations; n++) pkgTimes[pkgIndex++] = usecTimeMSRRead(PKG);

		printMSRReadTimeRecord(dramTimes, "DRAM");
		printMSRReadTimeRecord(gpuTimes, "GPU");
		printMSRReadTimeRecord(coreTimes, "CORE");
		printMSRReadTimeRecord(pkgTimes, "PKG");
	}


	public static void timeAllCFunctions(int iterations) {
		long[] profileInitTimes = new long[iterations];
		long[] getSocketTimes = new long[iterations];
		long[] energyStatTimes = new long[iterations];
		long[] profileDeallocTimes = new long[iterations];
		int profInitIndex = 0, getSocketIndex = 0, energyStatIndex = 0, profDeallocIndex = 0;
		for (int n = 0; n < iterations; n++) profileInitTimes[profInitIndex++] = usecTimeProfileInit();
		for (int n = 0; n < iterations; n++) getSocketTimes[getSocketIndex++] = usecTimeGetSocketNum();
		for (int n = 0; n < iterations; n++) energyStatTimes[energyStatIndex++] = usecTimeEnergyStatCheck(0);
		for (int n = 0; n < iterations; n++) {
			EnergyManager.profileInit(); // make sure memory is alloc'd first to prevent 'double free' errors
			profileDeallocTimes[profDeallocIndex++] = usecTimeProfileDealloc();
		}
		printFunctionTimeRecord(profileInitTimes,"profileInit()");
		printFunctionTimeRecord(getSocketTimes,"getSocketNum()");
		printFunctionTimeRecord(energyStatTimes,"energyStatCheck()");
		printFunctionTimeRecord(profileDeallocTimes,"profileDealloc()");

	}

	private static void printFunctionTimeRecord(long[] record, String name) {
		for (int i = 0; i < record.length; i++)
			System.out.println(name+": "+record[i]);
	}
	private static void printMSRReadTimeRecord(long[][] record, String name) {
		for (int i = 0; i < record.length; i++)
			for (int s = 0; s < record[i].length; s++)
				System.out.println(name + " Socket" + (s+1) + ": " + record[i][s]);
	}


	private static void usageAbort() {
		System.out.println(
				"\nusage: sudo java jrapltesting.RuntimeTestUtils <option> <number of iterations>" +
				"\n  option:" +
				"\n    --time-java-calls" +
				"\n    --time-native-calls" +
				"\n    --time-msr-readings"
			);
		System.exit(2);
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Reads command line argument and decides which of these methods to call.
	*	@param args Array of command line arguments. Format:
	*		OPTION NUM_ITERATIONS
	*		OPTION can be 
	*			--time-java-calls, which does the runtime of native calls from Java
	*			--time-native-calls, which does the runtime of native calls directly in C
	*			--time-msr-readings, which times how long it takes to access each MSR register when reading the energy consumption of each power domain
	*		NUM_ITERATIONS is the number of trials to run any of these options
	*/
	public static void main(String[] args)
	{
		EnergyManager manager = new EnergyManager();
		manager.init();

		int iterations;
		if(args.length != 2) {
			manager.dealloc();
			usageAbort();
		}
		try {
			iterations = Integer.parseInt(args[1]);
		} catch(NumberFormatException e){
			System.out.println("Illegal value for <number of iterations>");
			usageAbort();
			return;
		}

		if(args[0].equals("--time-java-calls")){ //Java function timing
			timeMethodMultipleIterations(() -> EnergyManager.profileInit(), "profileInit()", iterations);
			//timeMethodMultipleIterations(() -> ArchSpec.getSocketNum(), "getSocketNum()", iterations);
			timeMethodMultipleIterations(() -> EnergyMonitor.energyStatCheck(0), "energyStatCheck()", iterations);
			timeMethodMultipleIterations(() -> EnergyManager.profileDealloc(), "profileDealloc()", iterations);
		}
		else if(args[0].equals("--time-native-calls")){
			initCSideTiming();
			timeAllCFunctions(iterations);
			deallocCSideTiming();
		}
		else if (args[0].equals("--time-msr-readings")){
			initCSideTiming();
			timeAllMSRReads(iterations);
			deallocCSideTiming();
		}
		else {
			manager.dealloc();
			usageAbort();
		}

		manager.dealloc();
	}
}




