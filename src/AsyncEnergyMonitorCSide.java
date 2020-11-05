package jrapl;

public class AsyncEnergyMonitorCSide extends AsyncEnergyMonitor 
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
	private int storageType;

	//TODO -- consider tracking the lifetime of the monitor. timestamp between start and stop, already did it for AsynEnergyManager

	//constants to define energy sample storage method on the C side
	private static final int DYNAMIC_ARRAY_STORAGE = 1;
	private static final int LINKED_LIST_STORAGE = 2;

	@Override //from EnergyManager
	public void init()
	{
		super.init();
		id = nextAvailableId++;
		slightsetup();
		allocMonitor(id,samplingRate,storageType);
	}

	@Override //from EnergyManager
	public void dealloc()
	{
		deallocMonitor(id);
		super.dealloc();
	}

	public AsyncEnergyMonitorCSide(int s)
	{
		samplingRate = s;
		storageType = DYNAMIC_ARRAY_STORAGE; //default
	}

	public AsyncEnergyMonitorCSide(int s, String storageTypeString)
	{
		samplingRate = s;
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

	public void reset()
	{
		System.out.println("Coming soon...");
	}

	public String toString()
	{
		return "Coming soon...";
	}

	public static void main(String[] args)
	{
		AsyncEnergyMonitor a = new AsyncEnergyMonitorCSide(10,"DYNAMIC_ARRAY");
		a.init();

		a.start();
		try{ Thread.sleep(400);} catch(Exception e){}
		a.stop();

		a.writeToFile(null);

		a.dealloc();
	}

}
