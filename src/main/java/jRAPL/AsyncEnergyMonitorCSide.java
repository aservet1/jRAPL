package jRAPL;

//these two are just for testing in main(), they dont actually help the class
import java.util.Arrays;
import java.time.Duration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


public class AsyncEnergyMonitorCSide extends AsyncEnergyMonitor
{
	private native static void startCollecting();
	private native static void stopCollecting();
	private native static void cSideReset();

	private native static void allocMonitor(int samplingRate, int storageType);
	private native static void deallocMonitor();
	private native static void writeToFileFromC(String filePath);
	private native static String lastKSamples(int k);
	private native static long[] lastKTimestamps(int k);
	private native static int nSamples();

	private int samplingRate;
	private int storageType;

	//constants to define energy sample storage method on the C side
	private static final int DYNAMIC_ARRAY_STORAGE = 1;
	private static final int LINKED_LIST_STORAGE = 2;

	@Override //from EnergyManager
	public void init()
	{
		super.init();
		allocMonitor(samplingRate,storageType);
	}

	@Override //from EnergyManager
	public void dealloc()
	{
		deallocMonitor();
		super.dealloc();
	}

	public AsyncEnergyMonitorCSide()
	{
		samplingRate = 10;
		storageType = DYNAMIC_ARRAY_STORAGE; //default
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
		super.start();
		startCollecting();
	}

	public void stop()
	{
		super.stop();
		stopCollecting();
	}

	public void writeToFile(String filePath)
	{
		writeToFileFromC(filePath);
	}

	public String[] getLastKSamples(int k)
	{
		return lastKSamples(k).split("_");
	}

	public Instant[] getLastKTimestamps(int k)
	{
		long[] usecValues = lastKTimestamps(k);
		Instant[] instantValues = new Instant[usecValues.length];
		for (int i = 0; i < usecValues.length; i++)
			instantValues[i] = Instant.EPOCH.plus(usecValues[i], ChronoUnit.MICROS);
		return instantValues;
	}

	public int getNumSamples() {
		return nSamples();
	}

	@Override
	public void reset()
	{
		super.reset();
		cSideReset();
	}

	public String toString()
	{
		return "Coming soon...";
	}

}
