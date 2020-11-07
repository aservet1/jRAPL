package jrapltesting;
import jrapl.*;

public class GeneralTestDriver
{
	public static void main(String[] args)
	{
		EnergyManager manager = new EnergyManager();
		manager.init();
		System.out.println(ArchSpec.infoString());
		manager.dealloc();
	}

	private static void threadThing()
	{
		AsyncEnergyMonitorJavaSide aemonj = new AsyncEnergyMonitorJavaSide();
		aemonj.start();
		try { Thread.sleep(5000); }
		catch (Exception e) {}
		aemonj.stop();
		System.out.println(aemonj);
	}


}
