package jrapl;

public class AsyncEnergyMonitorCSide /*extends JRAPL*/
{
	public native static void startCollecting(int delay);

	public native static void stopCollecting();

	public static void main(String[] args)
	{
		JRAPL.loadLibrary();
		JRAPL.ProfileInit();

//		startCollecting(0);
		try{ Thread.sleep(5000);} catch(Exception e){}
//		stopCollecting();

		JRAPL.ProfileDealloc();
	}

}
