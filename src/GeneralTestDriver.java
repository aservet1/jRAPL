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
		try { Thread.sleep(100); } catch (Exception e) {}
		ec.stopReading();

		System.out.println(ec);

		EnergyCheckUtils.ProfileDealloc();
	}

}
