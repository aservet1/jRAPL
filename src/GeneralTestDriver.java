package jrapl;

import java.util.ArrayList;
import java.util.Arrays;

public class GeneralTestDriver
{
	
	public static void main(String[] args)
	{
		EnergyCheckUtils.ProfileInit();

		EnergyReadingCollector ec = new EnergyReadingCollector();

		ec.startReading();
		while (ec.getNumReadings() < 100);
		ec.stopReading();

		System.out.println(ec);

		EnergyCheckUtils.ProfileDealloc();
	}

}
