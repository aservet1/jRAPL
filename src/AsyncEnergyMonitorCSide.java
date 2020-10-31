package jrapl;

public class AsyncEnergyMonitorCSide /*extends JRAPL*/ implements AsyncMonitor
{
	private native static void slightsetup();

	private native static void startCollecting(int id);
	private native static void stopCollecting(int id);

	private native static void allocMonitor(int id, int samplingRate, int storageType);
	private native static void deallocMonitor(int id);
	private native static void writeToFile(int id, String filePath);
	private native static String lastKSamples(int id, int k);

	private static int nextAvailableId = 0;
	
	private int id;
	private int samplingRate;
	
	//TODO -- consider tracking the lifetime of the monitor. timestamp between start and stop, already did it for AsynEnergyManager

	//constants to define energy sample storage method on the C side
	private static final int DYNAMIC_ARRAY_STORAGE = 1;
	private static final int LINKED_LIST_STORAGE = 2;

	public AsyncEnergyMonitorCSide(int s)
	{
		id = nextAvailableId++;
		samplingRate = s;
		allocMonitor(id,samplingRate,DYNAMIC_ARRAY_STORAGE); // uses dynamic array by default
	}

	public AsyncEnergyMonitorCSide(int s, String storageTypeString)
	{
		id = nextAvailableId++;
		samplingRate = s;
		int storageType;
		switch (storageTypeString) {
			case "DYNAMIC_ARRAY":
				storageType = DYNAMIC_ARRAY_STORAGE;
				break;
			case "LINKED_LIST":
				storageType = LINKED_LIST_STORAGE;
				break;
			default:
				storageType = -1; // just to keep the compiler happy, but we're gonna exit in two seconds anyways
				System.err.println("Invalid storage type string: " + storageTypeString);
				System.exit(1);
		}
		allocMonitor(id,samplingRate,storageType);
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
		JRAPL.loadLibrary();
		JRAPL.ProfileInit();
		slightsetup();

		AsyncEnergyMonitorCSide a = new AsyncEnergyMonitorCSide(10,"DYNAMIC_ARRAY");

		a.start();
		try{ Thread.sleep(400);} catch(Exception e){}
		a.stop();

		a.writeToFile(null);

		JRAPL.ProfileDealloc();
	}

}
