package jrapltesting;
import jrapl.*;

public class GeneralTestDriver
{
	public static void main(String[] args)
	{
		JRAPL.loadLibrary();
		JRAPL.profileInit();
		System.out.println(ArchSpec.infoString());
		JRAPL.profileDealloc();
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
