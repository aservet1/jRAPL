package jrapl_testing;

import java.util.ArrayList;
import java.util.Arrays;
import jrapl.*;

public class GeneralTestDriver
{

	private static int fib(int n)
	{
		if (n <= 1) return 1;
		else return fib(n-1) + fib(n-2);
	}
	
	public static void main(String[] args)
	{
		JRAPL.ProfileInit();

		AsyncEnergyMonitorJavaSide ec = new AsyncEnergyMonitorJavaSide(0);

		ec.startReading();
		//while (ec.getNumReadings() < 100);
		try { Thread.sleep(5000); } catch (Exception e) {}
		//fib(42);
		ec.stopReading();

		if (args.length == 0) System.out.println(ec);
		else ec.writeToFile(args[0]);

		JRAPL.ProfileDealloc();
	}

}
