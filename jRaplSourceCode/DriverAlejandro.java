package jrapl;

import java.util.Arrays;

public class DriverAlejandro
{
	public static void main(String[] args)
	{
		long startTime, endTime;

			System.out.println("\n--------------\n < Running static block:\n");
			startTime = System.currentTimeMillis();
			EnergyCheckUtils e = new EnergyCheckUtils();
			endTime = System.currentTimeMillis();
			System.out.println(" < Static block took " + (endTime - startTime) + " ms");

			System.out.println("\n\n < Running ProfileInit():\n");
			startTime = System.currentTimeMillis();
			EnergyCheckUtils.ProfileInit();
			endTime = System.currentTimeMillis();
			System.out.println(" <  Profileinit() took " + (endTime - startTime) + " ms");

			System.out.println("\n--------------\n < Running GetSocketNum():\n");
			startTime = System.currentTimeMillis();
			EnergyCheckUtils.GetSocketNum();
			endTime = System.currentTimeMillis();
			System.out.println(" < GetSocketNum() took " + (endTime - startTime) + " ms");

			System.out.println("\n--------------\n < Running EnergyStatCheck():\n");
			startTime = System.currentTimeMillis();
			EnergyCheckUtils.EnergyStatCheck();
			endTime = System.currentTimeMillis();
			System.out.println(" < EnergyStatCheck() took " + (endTime - startTime) + " ms");

			System.out.println("\n--------------\n < Running getEnergyStats():\n");
			startTime = System.currentTimeMillis();
			double[] stats = EnergyCheckUtils.getEnergyStats();
			endTime = System.currentTimeMillis();
			System.out.println(" < getEnergyStats() took " + (endTime - startTime) + " ms");

	}
}
