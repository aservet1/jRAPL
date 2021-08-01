package jRAPL;

import java.time.Instant;

public class AsyncEnergyMonitorCSide extends AsyncEnergyMonitor
{
	private native static void startNative();
	private native static void stopNative();
	private native static void resetNative();
	private native static void activateNative(int samplingRate,int storageType,int size_parameter);
	private native static void deactivateNative();
	private native static void writeFileCSVNative(String filePath);
	private native static String getLastKSamplesNative(int k);
	private native static long[] getLastKTimestampsNative(int k);
	private native static int getNumSamplesNative();
	private native static void setSamplingRateNative(int s);
	private native static int getSamplingRateNative();

	// these correspond to '#define' macros written in AsyncEnergyMonitor.h
	private static final int DYNAMIC_ARRAY_STORAGE = 1;
	private static final int LINKED_LIST_STORAGE = 2;

	// @TODO make these final ?
	private int samplingRate;
	private int storageType;
	private int initialSize = 128;

	public AsyncEnergyMonitorCSide() { 
		samplingRate = 10;
		storageType = DYNAMIC_ARRAY_STORAGE;
	}

	public AsyncEnergyMonitorCSide(int s, String storageTypeString, int size) {
		samplingRate = s;
		initialSize = size;
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
	public AsyncEnergyMonitorCSide(String storageTypeString) {
		switch (storageTypeString) { // @TODO consider if you want to do a setStorageType() method...that might be a hell of a C-Side carfuffle though
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

	@Override //from EnergyManager
	public void activate() {
		super.activate();
		activateNative(samplingRate,storageType,initialSize);
	}

	@Override
	public void deactivate() {
		deactivateNative();
		super.deactivate();
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
	public void writeFileCSV(String filePath) {
		writeFileCSVNative(filePath);
	}

	@Override
	public String[] getLastKSamples(int k) {
		// I don't know how to do JNI String arrays,
		// so return one giant '_'-delimited string to split
		return getLastKSamplesNative(k).split("_");
	} // this is a potential time and memory overhead hazard

	@Override
	public Instant[] getLastKTimestamps(int k) {
		long[] usecValues = getLastKTimestampsNative(k);
		Instant[] instantValues = new Instant[usecValues.length];
		for (int i = 0; i < usecValues.length; i++)
			instantValues[i] = Utils.usecToInstant(usecValues[i]);
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
