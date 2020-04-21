package jrapl;

import java.util.Arrays;
import java.text.ParseException;
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
		Instant start, end;
		start = Instant.now();
		method.run();
		end = Instant.now();
		long elapsed = Duration.between(start, end).toNanos() / 1000;
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
			if (name == "ProfileDealloc()") EnergyCheckUtils.ProfileInit(); // prevents a 'double free or corruption' error
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
		for (i = 0; i < results.length; i++)
			System.out.println(name + ": " + results[i]);
	}

	/* Runs all the native calls x amount of times. Assumes they're being timed
		 and are printing results on the C side of things */
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

	public static void DramCorePackageStats(int iters)
	{
		EnergyReadings data = getReadings(iters);
		printDiffs(data, "DRAM", 0);
		printDiffs(data, "CORE", 1);
		printDiffs(data, "PACKAGE", 2);
		EnergyCheckUtils.ProfileDealloc();
	}


	public static void main(String[] args)
	{
		//get the static block out of the way by making this useless object
		new EnergyCheckUtils();
		int iterations;
		if(args.length != 2){
			System.out.println("\n\nFORMAT: java jrapl.RuntimeTestUtils [OPTIONS [NUM_ITERATIONS]]\nOPTIONS\n\t--time-java-calls\n\t--time-native-calls\n\t--time-msr-readings\n\t--read-energy-values");
			return;
		}
		boolean timingFunctionCalls = (args[0].equals("--time-native-calls")), timingMsrReadings = (args[0].equals("--time-msr-readings"));
		try{
			iterations = Integer.parseInt(args[1]);
		}
		catch(NumberFormatException e){
			System.out.println("Illegal value for NUM_ITERATIONS");
			return;
		}

		if(args[0].equals("--time-java-calls")){ //Java function timing
			timeItStats(EnergyCheckUtils::ProfileInit, "ProfileInit()", iterations);
			timeItStats(EnergyCheckUtils::GetSocketNum, "GetSocketNum()", iterations);
			timeItStats(EnergyCheckUtils::EnergyStatCheck, "EnergyStatCheck()", iterations);
			timeItStats(EnergyCheckUtils::ProfileDealloc, "ProfileDealloc()", iterations);
		}
		else if(args[0].equals("--read-energy-values")){ //Timing and reading energy register
			DramCorePackageStats(iterations);
		}
		else if(timingFunctionCalls || timingMsrReadings){
			EnergyCheckUtils.StartTimeLogs(iterations, timingFunctionCalls, timingMsrReadings);
			for (int i = 0; i < iterations; i++) {
				EnergyCheckUtils.ProfileInit();
				EnergyCheckUtils.GetSocketNum();
				EnergyCheckUtils.EnergyStatCheck();
				EnergyCheckUtils.ProfileDealloc();
			}
			EnergyCheckUtils.FinalizeTimeLogs();
		}
		else 
			System.out.println("\n\nFORMAT: java jrapl.RuntimeTestUtils [OPTIONS [NUM_ITERATIONS]]\nOPTIONS\n\t--time-java-calls\n\t--time-native-calls\n\t--time-msr-readings\n\t--read-energy-values");


	}
}
