package jRAPL;

import java.time.Instant;

public class AsyncEnergyMonitorCSide extends AsyncEnergyMonitor
{
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
		NativeAccess.activateMonitor(samplingRate,storageType,initialSize);
	}

	@Override
	public void deactivate() {
		NativeAccess.deactivateMonitor(); // @TODO Have an AsyncEnergyMonitor sub-module for the NativeAccess class
		super.deactivate();
	}

	@Override
	public void start() {
		super.start();
		NativeAccess.startMonitor();
	}

	@Override
	public void stop() {
		super.stop();
		NativeAccess.stopMonitor();
	}

	@Override
	public void writeFileCSV(String filePath) {
		NativeAccess.writeFileCSVMonitor(filePath);
	}

	@Override
	public String[] getLastKSamples(int k) {
		// I don't know how to do JNI String arrays,
		// so return one giant '_'-delimited string to split
		return NativeAccess.getLastKSamplesMonitor(k).split("_");
	} // this is a potential time and memory overhead hazard

	@Override
	public int getNumSamples() {
		return NativeAccess.getNumSamplesMonitor();
	}

	@Override
	public int getSamplingRate() {
		return NativeAccess.getSamplingRateMonitor();
	}

	@Override
	public void setSamplingRate(int s) {
		NativeAccess.setSamplingRateMonitor(s);
	}

	@Override
	public void reset() {
		super.reset();
		NativeAccess.resetMonitor();
	}
}
