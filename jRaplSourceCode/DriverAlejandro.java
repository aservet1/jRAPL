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
			long time = timeIt(method);
			//System.out.println(time);
			results[i++] = time;
		}
		System.out.println("Avg:\t" + average(results));
		System.out.println("StDev:\t" + stdev(results));
		Arrays.sort(results);
		System.out.println(Arrays.toString(results));
	}

	public static void main(String[] args)
	{
		//get the static block out of the way by making this useless object
		EnergyCheckUtils e = new EnergyCheckUtils();

		int iterations = Integer.parseInt(args[0]);
		//timeItStats(EnergyCheckUtils::GetSocketNum, "GetSocketNum", iterations);
		//timeItStats(EnergyCheckUtils::EnergyStatCheck, "EnergyStatCheck", iterations);

		for (int i = 0; i < iterations; i++) {
			EnergyCheckUtils.ProfileInit();
			EnergyCheckUtils.ProfileDealloc();
		} System.out.println("\n");
		EnergyCheckUtils.ProfileInit();

		for (int i = 0; i < iterations; i++) {
			EnergyCheckUtils.GetSocketNum();
		}

		for (int i = 0; i < iterations; i++) {
			EnergyCheckUtils.EnergyStatCheck();
		}

		EnergyCheckUtils.ProfileDealloc();

	}
}
