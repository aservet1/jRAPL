package jrapl;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Arrays;
//import java.lang.invoke.MethodHandles;

public class EnergyCheckUtils {
	/// can't find the definitons of these anywhere........
	public native static int scale(int freq);                   // not used yet in this file
	public native static int[] freqAvailable();                 // not used yet in this file

	/// related to msr.c functions
	public native static double[] GetPackagePowerSpec();        // msr.c -- getPowerSpec() with parameter specified for domain
	public native static double[] GetDramPowerSpec();           // msr.c -- getPowerSpec() with parameter specified for domain
	public native static void SetPackagePowerLimit(int socketId, int level, double costomPower);        // not used yet in this file
	public native static void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin); // not used yet in this file
	public native static void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);    // not used yet in this file
	public native static void SetDramPowerLimit(int socketId, int level, double costomPower);           // not used yet in this file
	public native static void SetPowerLimit(int ENABLE);       // not used yet in this file

	/** Documentation not done. See CPUScaler.c for source
	 *  Initializes the energy profile of the system. To be called before accessing any jRAPL utility.
	 *  Information initialized (stored entirely in static global variables on the C side):
	 *  	CPU Model
	 *  	Number of CPU sockets
	 *  	Array of file handles for MSR readings
	 *
	 *  @return wraparoundValue -- 
	 *
	*/
	public native static int ProfileInit();

	/**
	 *  @return Number of CPU sockets the current system has
	*/
	public native static int GetSocketNum();

	/** Returns a string with current total energy consumption reported in MSR registers.
	 *	Formatted " 1stSocketInfo @ 2ndSocketInfo @ ... @ NthSocketInfo " with @ delimiters
	 *	Each NthSocketInfo subsection formatted " dram_energy # cpu_energy # package_energy " with # delimieters
	 *	This string gets parsed into an array in getEnergyStats().
	*/
	public native static String EnergyStatCheck();

	/** Free all memory allocated in ProfileInit()
	*/
	public native static void ProfileDealloc();

	/**  Represents the energy in the rapl unit in a way that prevents bit overflow that would cause negative values. */
	public static int wraparoundValue;
	/** Number of sockets CPU has. Determined in ProfileInit() method. */
	public static int socketNum;

	/// the static block loads the library of native C calls from the JAR. also initializes a profile and gets number of CPU sockets
	static {
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
			/*Lookup cl = MethodHandles.privateLookupIn(ClassLoader.class, MethodHandles.lookup());
			VarHandle sys_paths = cl.findStaticVarHandle(ClassLoader.class, "sys_paths", String[].class);
			sys_paths.set(null);*/
		} catch (Exception e) { }

		try {
			NativeUtils.loadLibraryFromJar("/home/alejandro/jRAPL/jRaplSourceCode/libCPUScaler.so");
		} catch (Exception e) {
			e.printStackTrace();
		}
		wraparoundValue = ProfileInit();
		socketNum = GetSocketNum();
	}

	/**
	 * Parses string generated from the native EnergyStatCheck() method into an array of doubles.
	 * @return an array of current energy information.
	 * Array will be size (3 * socketnum). There will be three entries per socket
	 * The first entry is: Dram/uncore gpu energy (depends on the cpu architecture)
	 * The second entry is: CPU energy
	 * The third entry is: Package energy
	 * General layout of the array:
	 * 	[dram_s1, cpu_s1, pkg_s1, dram_s2, cpu_s2, pkg_s2, ... , dram_sn, cpu_sn, pkg_sn]
	 *	sn means socket number associated with this reading for all n greater than 1 	//@TODO -- is socket numbering 0 indexed or 1 indexed?
	*/
	public static double[] getEnergyStats() {
		socketNum = GetSocketNum(); //@TODO -- is this redundant? can we just assume that it was already set during the sstatic block?
		String EnergyInfo = EnergyStatCheck();
		/*One Socket*/
		if(socketNum == 1) {
			double[] stats = new double[3]; // 3 stats per socket
			String[] energy = EnergyInfo.split("#");

			//System.out.println(Arrays.toString(energy));
			stats[0] = Double.parseDouble(energy[0]);
			stats[1] = Double.parseDouble(energy[1]);
			stats[2] = Double.parseDouble(energy[2]);

			return stats;

		} else {
		/*Multiple sockets*/
			String[] perSockEner = EnergyInfo.split("@");
			double[] stats = new double[3*socketNum]; // 3 stats per socket
			int count = 0;


			for(int i = 0; i < perSockEner.length; i++) {
				String[] energy = perSockEner[i].split("#");
				for(int j = 0; j < energy.length; j++) {
					count = i * 3 + j;	//accumulative count
					stats[count] = Double.parseDouble(energy[j]);
				}
			}
			return stats;
		}
	}

	/** Frees memory allocated by ProfileInit(). Called when energy reading utility is done
	*/
	public static void DeallocProfile() {
		ProfileDealloc();
	}

	//Native calls for timing purposes
	public native static void StartTimeLogs(int logLength, boolean timingFunctionCalls, boolean timingMsrReadings);
	public native static void FinalizeTimeLogs();


	public static void main(String[] args)
	{
		EnergyReadingCollector ec = new EnergyReadingCollector();

		ec.startReading();
		for (int x = 0; x < 1000000; x++) getEnergyStats();
		ec.stopReading();

		System.out.println(ec);

		DeallocProfile();
	}

}
