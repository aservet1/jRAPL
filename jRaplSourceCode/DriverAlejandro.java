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

	public static void main(String[] args)
	{
		//repeatGetEnergyStats(10);
		long startTime, endTime;

		System.out.println("\n--------------------------------------------\n < Running static block:\n");
		startTime = System.nanoTime();
		EnergyCheckUtils e = new EnergyCheckUtils();
		endTime = System.nanoTime();
		System.out.println("\n < Static block took " + (endTime - startTime) + " nanoseconds");

		System.out.println("\n--------------------------------------------\n < Running ProfileInit():\n");
		startTime = System.nanoTime();
		EnergyCheckUtils.ProfileInit();
		endTime = System.nanoTime();
		System.out.println("\n <  Profileinit() took " + (endTime - startTime) + " nanoseconds");

		System.out.println("\n--------------------------------------------\n < Running GetSocketNum():\n");
		startTime = System.nanoTime();
		EnergyCheckUtils.GetSocketNum();
		endTime = System.nanoTime();
		System.out.println("\n < GetSocketNum() took " + (endTime - startTime) + " nanoseconds");

		System.out.println("\n--------------------------------------------\n < Running EnergyStatCheck():\n");
		startTime = System.nanoTime();
		EnergyCheckUtils.EnergyStatCheck();
		endTime = System.nanoTime();
		System.out.println("\n < EnergyStatCheck() took " + (endTime - startTime) + " nanoseconds");

		System.out.println("\n--------------------------------------------\n < Running getEnergyStats():\n");
		startTime = System.nanoTime();
		double[] stats = EnergyCheckUtils.getEnergyStats();
		endTime = System.nanoTime();
		System.out.println("\n < getEnergyStats() took " + (endTime - startTime) + " nanoseconds");
	}
}
