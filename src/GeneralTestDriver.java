package jrapltesting;
import jrapl.*;

public class GeneralTestDriver
{
	public static void main(String[] args)
	{
		AsyncEnergyMonitorJavaSide aemonj = new AsyncEnergyMonitorJavaSide();
		aemonj.start();
		try { Thread.sleep(5000); }
		catch (Exception e) {}
		aemonj.stop();
		System.out.println(aemonj);
	}

	private static void bothMemoryThings()
	{
		memoryThing("Object");
		memoryThing("Array");
	}
}
