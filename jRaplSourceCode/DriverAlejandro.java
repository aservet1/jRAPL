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
			long time = timeIt(method);
			System.out.println(time+"; "+x);
			results[i++] = time;
		}
		System.out.println("Avg:\t" + average(results));
		System.out.println("StDev:\t" + stdev(results));
	}

	public static void main(String[] args)
	{
		EnergyCheckUtils e = new EnergyCheckUtils();

		timeItStats(EnergyCheckUtils::getEnergyStats, "getEnergyStats", 5000);

		/*timeIt(EnergyCheckUtils::ProfileInit, "ProfileInit");
		timeIt(EnergyCheckUtils::GetSocketNum, "GetSocketNum");
		timeIt(EnergyCheckUtils::EnergyStatCheck, "EnergyStatCheck");
		timeIt(EnergyCheckUtils::getEnergyStats, "getEnergyStats");
		*/
	}
}
