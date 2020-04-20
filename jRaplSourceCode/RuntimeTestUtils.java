package jrapl;

import java.util.Arrays;

public class DriverAlejandro
{
	public static void repeatGetEnergyStats()
	{
		for (int i = 0;; i++)
		{
			EnergyCheckUtils.getEnergyStats();
			try { Thread.sleep(1000L); } catch (Exception e) { System.out.println("thread error"); }
		}
	}

	/* Times a method call, returns time in microseconds */
	public static long timeIt(Runnable method)
	{
		long startTime, endTime;
		startTime = System.nanoTime();
		method.run();
		endTime = System.nanoTime();
		long elapsed = (endTime - startTime) / 1000;
		return elapsed;
	}

	public static void run500(Runnable method)
	{
		for (int x = 0; x < 500; x++)
			method.run();
	}

	private static double average(long[] results)
	{
		long total = 0;
		for (int i = 0; i < results.length; i++)
			total += results[i];
		return total / results.length;
	}

	private static double stdev(long[] results)
	{
		int N = results.length;
		double avg = average(results);
		int deviationSum = 0;
		for (int i = 0; i < results.length; i++)
			deviationSum += Math.pow((results[i]-avg),2);
		return Math.sqrt(deviationSum/(N-1));
	}

	public static void timeItStats(Runnable method, String name, int iterations)
	{
		int i = 0;
		long[] results = new long[iterations];
		for (int x = 0; x < iterations; x++) {
			if (name == "ProfileDealloc") EnergyCheckUtils.ProfileInit(); // prevents a 'double free or corruption' error
			long time = timeIt(method);
			//System.out.println(time);
			results[i++] = time;
		}
		System.out.println("-----------------------------------");
		System.out.println("Results for function: \'" + name +"\'");
		System.out.println("Avg:\t" + average(results));
		System.out.println("StDev:\t" + stdev(results));
		System.out.println("-----------------------------------");
		//Arrays.sort(results);
		System.out.println(Arrays.toString(results));
	}

	/* Runs all the native calls x amount of times. Assumes they're being timed
		 and are printing results on the C side of things */
	public static void runABunchOfNativeCalls(int x)
	{
		for (int i = 0; i < x; i++) {
			EnergyCheckUtils.ProfileInit();
			EnergyCheckUtils.ProfileDealloc();
		}
		System.out.println("\n");
		EnergyCheckUtils.ProfileInit();
		for (int i = 0; i < x; i++) {
			EnergyCheckUtils.GetSocketNum();
		}
		for (int i = 0; i < x; i++) {
			EnergyCheckUtils.EnergyStatCheck();
		}
		EnergyCheckUtils.ProfileDealloc();
	}

	/*Assumes C side is set up time and print out the time it takes to do each MSR reading*/
	public static void timePerSocketPerMsrReadings(int iterations)
	{
		for (int x = 0; x < iterations; x++)
			EnergyCheckUtils.EnergyStatCheck();
	}

// index can be 0 (DRAM), 2 (CORE), 3 (PACKAGE) -- index from getenergyStats()
// name should store the identifier for each line
// iters is the number of iterations
	public static void Stats(int index, String name, int iters){
		double[] before;
		double[] after;
		double reading;
		double totalTime = 0;
		int numReadings = 0;
		int totalNonZero = 0;
		Instant timeAtLastNonZero = Instant.now();
		Instant timeAtThisNonZero = null;
		double totalEnergy = 0;
		int lastNonZero = 0;
		while(iters > numReadings) {
			before = getEnergyStats();
			after = getEnergyStats();
			reading = after[index] - before[index];
			if(reading != 0){
				timeAtThisNonZero = Instant.now();
				long timediff = Duration.between(timeAtLastNonZero, timeAtThisNonZero).toNanos()/1000;
				System.out.println(name + " " + reading + " " + timediff + " " + lastNonZero);
				totalTime += timediff;
				lastNonZero = 0;
				totalNonZero += 1;
				totalEnergy += reading;
				timeAtLastNonZero = Instant.now();
			}
			else{
				lastNonZero += 1;
			}
			numReadings += 1;
		}
		System.out.println(name + " totals: " + totalEnergy + " " + totalNonZero + " " + totalTime + " " + iters);
	}

	public static void DramCorePackageStats()
	{
		Stats(0, "DRAM", 100000);
		Stats(1, "CORE", 100000);
		Stats(2, "PACKAGE", 100000);
		ProfileDealloc();
	}

	public static void main(String[] args)
	{
		//get the static block out of the way by making this useless object
		new EnergyCheckUtils();
		//if(args.length > 0)
		//timePerSocketPerMsrReadings(Integer.parseInt(args[0]));
		for (int x = 0; x < 100; x++) EnergyCheckUtils.getEnergyStats();
		//timeItStats(EnergyCheckUtils::GetSocketNum, "GetSocketNum", iterations);
		//timeItStats(EnergyCheckUtils::EnergyStatCheck, "EnergyStatCheck", iterations);
		/*timeItStats(EnergyCheckUtils::ProfileInit, "ProfileInit", iterations);
		timeItStats(EnergyCheckUtils::GetSocketNum, "GetSocketNum", iterations);
		timeItStats(EnergyCheckUtils::EnergyStatCheck, "EnergyStatCheck", iterations);
		timeItStats(EnergyCheckUtils::ProfileDealloc, "ProfileDealloc", iterations);*/
		//runABunchOfNativeCalls(iterations);


	}
}
