package jrapl;

public class AsyncEnergyMonitorCSide extends JRAPL //implements AsyncMonitor
{
	private native static void slightsetup();

	private native static void startCollecting(int id);
	private native static void stopCollecting(int id);

	private native static void allocMonitor(int id);
	private native static void deallocMonitor(int id);
	private native static void writeToFile(int id, String filePath);
	private native static String lastKSamples(int id, int k);  // just has a dummy return value right now...

	private static int nextAvailableId = 0;
	
	private int id;
	private int samplingRate;
	
	static {
		slightsetup();
	}

	public AsyncEnergyMonitorCSide(int s)
	{
		id = nextAvailableId++;
		samplingRate = s;
		allocMonitor(id);
	}

	public void start()
	{
		startCollecting(id);
	}

	public void stop()
	{
		stopCollecting(id);
	}

	public void writeToFile(String filePath)
	{
		writeToFile(id, filePath);
	}

	public String lastKSamples(int k)
	{
		String lastKString = lastKSamples(id,k);

		return lastKString;
	}

	public static void main(String[] args)
	{
		JRAPL.ProfileInit();

		AsyncEnergyMonitorCSide a = new AsyncEnergyMonitorCSide(10);
		AsyncEnergyMonitorCSide b = new AsyncEnergyMonitorCSide(10);
		//AsyncEnergyMonitorCSide c = new AsyncEnergyMonitorCSide(10);

		a.start();
		try{ Thread.sleep(100);} catch(Exception e){}
		a.stop();

		a.writeToFile(null);

		System.out.println(a.lastKSamples(7));

		JRAPL.ProfileDealloc();
	}

}
