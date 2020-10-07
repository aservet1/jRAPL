package jrapl;

public class AsyncEnergyMonitorCSide extends JRAPL implements AsyncMonitor
{
	private native static void slightsetup();

	private native static void startCollecting(int id);
	private native static void stopCollecting(int id);

	private native static void allocMonitor(int id, int samplingRate);
	private native static void deallocMonitor(int id);
	private native static void writeToFile(int id, String filePath);
	private native static String lastKSamples(int id, int k);

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
		allocMonitor(id,samplingRate);
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

	public String[] lastKSamples(int k)
	{
		String lastKString = lastKSamples(id,k);

		return lastKString.split("_");
	}

	public void reInit()
	{
		System.out.println("Coming soon...");
	}

	public static void main(String[] args)
	{
		JRAPL.ProfileInit();

		AsyncEnergyMonitorCSide a = new AsyncEnergyMonitorCSide(10);
		AsyncEnergyMonitorCSide b = new AsyncEnergyMonitorCSide(10);
		//AsyncEnergyMonitorCSide c = new AsyncEnergyMonitorCSide(10);

		a.start();
		try{ Thread.sleep(50);} catch(Exception e){}
		b.start();
		try{ Thread.sleep(50);} catch(Exception e){}
		a.stop();
		b.stop();

		System.out.println("A: ~~~ ");
		a.writeToFile(null);
		System.out.println("B: ~~~ ");
		b.writeToFile(null);

		JRAPL.ProfileDealloc();
	}

}
