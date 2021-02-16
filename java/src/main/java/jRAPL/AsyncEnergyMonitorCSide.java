package jRAPL;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AsyncEnergyMonitorCSide extends AsyncEnergyMonitor
{
	private native static void startNative();
	private native static void stopNative();
	private native static void resetNative();
	private native static void initNative(int samplingRate, int storageType);
	private native static void deallocNative();
	private native static void writeToFileNative(String filePath);
	private native static String getLastKSamplesNative(int k);
	private native static long[] getLastKTimestampsNative(int k);
	private native static int getNumSamplesNative();
	private native static void setSamplingRateNative(int s);
	private native static int getSamplingRateNative();

	// correspond to '#define' macros written in AsyncEnergyMonitor.h
	private static final int DYNAMIC_ARRAY_STORAGE = 1;
	private static final int LINKED_LIST_STORAGE = 2;

	// private int samplingRate;
	private int storageType;

	public AsyncEnergyMonitorCSide() { 
		samplingRate = 10;
		storageType = DYNAMIC_ARRAY_STORAGE;
	}

	public AsyncEnergyMonitorCSide(int s, String storageTypeString) {
		samplingRate = s;
		switch (storageTypeString) {
			case "DYNAMIC_ARRAY":
				storageType = DYNAMIC_ARRAY_STORAGE;
				break;
			case "LINKED_LIST":
				storageType = LINKED_LIST_STORAGE;
				break;
			default:
				System.err.println("Invalid storage type string: " + storageTypeString);
				System.exit(1);
		}
	}

	public AsyncEnergyMonitorCSide(int s) {
		samplingRate = s;
		storageType = DYNAMIC_ARRAY_STORAGE; //default
	}

	@Override //from EnergyManager
	public void init() {
		super.init();
		initNative(samplingRate,storageType);
	}

	@Override
	public void dealloc() {
		deallocNative();
		super.dealloc();
	}

	@Override
	public void start() {
		super.start();
		startNative();
	}

	@Override
	public void stop() {
		super.stop();
		stopNative();
	}

	@Override
	public void writeToFile(String filePath) {
		writeToFileNative(filePath);
	}

	@Override
	public String[] getLastKSamples(int k) {
		// I don't know how to do JNI String arrays,
		// so return '_' delimited string to split
		return getLastKSamplesNative(k).split("_");
	} // this is a potential time and memory overhead hazard

	@Override
	public Instant[] getLastKTimestamps(int k) {
		long[] usecValues = getLastKTimestampsNative(k);
		Instant[] instantValues = new Instant[usecValues.length];
		for (int i = 0; i < usecValues.length; i++)
			instantValues[i] = Instant.EPOCH.plus(usecValues[i], ChronoUnit.MICROS);
		return instantValues;
	}

	@Override
	public int getNumSamples() {
		return getNumSamplesNative();
	}

	@Override
	public int getSamplingRate() {
		return getSamplingRateNative();
	}

	@Override
	public void setSamplingRate(int s) {
		setSamplingRateNative(s);
	}

	@Override
	public void reset() {
		super.reset();
		resetNative();
	}
}