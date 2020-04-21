package jrapl;

import java.util.Arrays;
import java.time.Duration;
import java.time.Instant;

public class RuntimeTestUtils
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
	public static class EnergyReadings{
		public double[][] energyStats;
		public Instant times[];

		public EnergyReadings(int iters){
			energyStats = new double[iters][3];
			times = new Instant[iters];
		}
	}

	public static void printDiffs(EnergyReadings data, String name, int index){ //if data != null, prints all the changes in the values from the energyStats in data followed by a summary of the totals, else prints nothing
		if(data == null){
			return;
		}
		Instant timeAtLastNonZero = data.times[0];
		Instant timeAtThisNonZero = null;
		long totalTime = 0;
		long timeDiff = 0;
		double[] before = data.energyStats[0];
		double[] after = null;
		int lastNonZero = 0;
		int totalNonZero = 0;
		double reading = 0;
		double totalEnergy = 0;

		for(int i = 1; i < data.times.length; i++){
			after = data.energyStats[i];
			reading = after[index] - before[index];
			if(reading != 0){
				timeAtThisNonZero = data.times[i];
				timeDiff = Duration.between(timeAtLastNonZero, timeAtThisNonZero).toNanos() / 1000;
				System.out.println(name + " " + reading + " " + timeDiff + " " + lastNonZero);
				totalTime += timeDiff;
				lastNonZero = 0;
				totalNonZero += 1;
				totalEnergy += reading;
				timeAtLastNonZero = timeAtThisNonZero;
				before = after;
		}
			else{
				lastNonZero += 1;
			}
		}
		System.out.println(name + " totals: " + totalEnergy + " " + totalNonZero + " " + totalTime + " " + data.times.length);
	}
	public static EnergyReadings getReadings(int iters){ //Runs the getEnergyStats function `iter` number of times
		EnergyReadings data = new EnergyReadings(iters);
		int i = 0;
		while(i < iters) {
			data.energyStats[i] = EnergyCheckUtils.getEnergyStats();
			data.times[i] = Instant.now();
			i++;
		}
		return data;
	}

	public static void DramCorePackageStats()
	{
		EnergyReadings data = getReadings(100000);
		printDiffs(data, "DRAM", 0);
		printDiffs(data, "CORE", 1);
		printDiffs(data, "PACKAGE", 2);
		EnergyCheckUtils.ProfileDealloc();
	}

	public static void main(String[] args)
	{
		//get the static block out of the way by making this useless object
		new EnergyCheckUtils();
		//if(args.length > 0)
		//timePerSocketPerMsrReadings(Integer.parseInt(args[0]));
		//for (int x = 0; x < 100; x++) EnergyCheckUtils.getEnergyStats();
		DramCorePackageStats();
		//timeItStats(EnergyCheckUtils::GetSocketNum, "GetSocketNum", iterations);
		//timeItStats(EnergyCheckUtils::EnergyStatCheck, "EnergyStatCheck", iterations);
		/*timeItStats(EnergyCheckUtils::ProfileInit, "ProfileInit", iterations);
		timeItStats(EnergyCheckUtils::GetSocketNum, "GetSocketNum", iterations);
		timeItStats(EnergyCheckUtils::EnergyStatCheck, "EnergyStatCheck", iterations);
		timeItStats(EnergyCheckUtils::ProfileDealloc, "ProfileDealloc", iterations);*/
		//runABunchOfNativeCalls(iterations);


	}
}
