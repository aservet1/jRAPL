package jrapl;

public class AsyncEnergyMonitorCSide extends JRAPL
{
	public native static void startCollecting();
	public native static void stopCollecting();

	public native static void initCollector();
	public native static void freeCollector();
	public native static void writeToFile(String filePath);


	public static void main(String[] args)
	{
		JRAPL.ProfileInit();

		initCollector();

		startCollecting();
		try{ Thread.sleep(100);} catch(Exception e){}
		stopCollecting();

		freeCollector();

		writeToFile(null);

		JRAPL.ProfileDealloc();
	}

}
