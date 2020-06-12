package jrapl;

import java.util.ArrayList;
import java.util.Arrays;

public class GeneralTestDriver
{
	
	public static void main(String[] args)
	{
		JRAPL.ProfileInit();

		EnergyReadingCollector ec = new EnergyReadingCollector();

		ec.startReading();
		//while (ec.getNumReadings() < 100);
		try { Thread.sleep(10000); } catch (Exception e) {}
		ec.stopReading();

		System.out.println(ec);

		JRAPL.ProfileDealloc();
	}

}
