package jRAPL;

import java.time.Instant;

public class AsyncEnergyMonitorCSide extends AsyncEnergyMonitor
{
	// private native static void start();
	// private native static void stop();
	// private native static void reset();
	// private native static void activate(int samplingRate,int storageType,int size_parameter);
	// private native static void deactivate();
	// private native static void writeFileCSV(String filePath);
	// private native static String getLastKSamples(int k);
	// private native static long[] getLastKTimestamps(int k);
	// private native static int getNumSamples();
	// private native static void setSamplingRate(int s);
	// private native static int getSamplingRate();

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
		JNIAccess.activate(samplingRate,storageType,initialSize);
	}

	@Override
	public void deactivate() {
		JNIAccess.deactivate(); // @TODO Have an AsyncEnergyMonitor sub-module for the JNIAccess class
		super.deactivate();
	}

	@Override
	public void start() {
		super.start();
		JNIAccess.start();
	}

	@Override
	public void stop() {
		super.stop();
		JNIAccess.stop();
	}

	@Override
	public void writeFileCSV(String filePath) {
		JNIAccess.writeFileCSV(filePath);
	}

	@Override
	public String[] getLastKSamples(int k) {
		// I don't know how to do JNI String arrays,
		// so return one giant '_'-delimited string to split
		return JNIAccess.getLastKSamples(k).split("_");
	} // this is a potential time and memory overhead hazard

	@Override
	public Instant[] getLastKTimestamps(int k) {
		long[] usecValues = JNIAccess.getLastKTimestamps(k);
		Instant[] instantValues = new Instant[usecValues.length];
		for (int i = 0; i < usecValues.length; i++)
			instantValues[i] = Utils.usecToInstant(usecValues[i]);
		return instantValues;
	}

	@Override
	public int getNumSamples() {
		return JNIAccess.getNumSamples();
	}

	@Override
	public int getSamplingRate() {
		return JNIAccess.getSamplingRate();
	}

	@Override
	public void setSamplingRate(int s) {
		JNIAccess.setSamplingRate(s);
	}

	@Override
	public void reset() {
		super.reset();
		JNIAccess.reset();
	}
}
