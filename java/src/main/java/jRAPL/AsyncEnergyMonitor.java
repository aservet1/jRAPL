
package jRAPL;

import java.time.Instant;
import java.time.Duration;

import java.util.Arrays; // just for the test driver, not actually used in the code

public abstract class AsyncEnergyMonitor extends EnergyMonitor {

	protected Instant monitorStartTime = null;
	protected Instant monitorStopTime = null;
	protected boolean isRunning = false;

	public abstract void writeToFile(String fileName);
	public abstract String[] getLastKSamples(int k);
	public abstract Instant[] getLastKTimestamps(int k);
	public abstract int getNumSamples();
	public abstract void setSamplingRate(int s);
	public abstract int getSamplingRate();

	@Override
	public void init() { super.init(); }
	
	@Override
	public void dealloc() { super.dealloc(); }

	public Duration getLifetime()
	{
		if (monitorStartTime != null && monitorStopTime != null)
			return Duration.between(monitorStartTime, monitorStopTime);
		else return null;
	}

	public void start()
	{
		isRunning = true;
		monitorStartTime = Instant.now();
	}

	public void stop()
	{
		monitorStopTime = Instant.now();
		isRunning = false;
	}

	public void reset()
	{
		monitorStartTime = null;
		monitorStopTime = null;
	}

	/**	
		Returns an array of arrays of EnergyStats objects. Each individual array
		is a list of the readings for all sockets requested. Even if only one
		socket was read from, it's still an array of arrays. The single socket
		reading is just index 0 of a 1-element array, regardless of whether it's
		just one socket because you asked for a specific socket, or because you
		were reading all sockets but only had one.
	*/
	public EnergyStats[] getLastKSamples_Objects(int k) 
	{
		String[] strings = getLastKSamples(k);
		Instant[] timestamps = getLastKTimestamps(k);

		EnergyStats[] samplesArray = new EnergyStats[k];
		for (int i = 0; i < strings.length; i++) {
			String energyString = strings[i];
			samplesArray[i] = Utils.stringToEnergyStats(energyString);
			samplesArray[i].setTimestamp(timestamps[i]);
		}

		return samplesArray;
	}

	public double[][] getLastKSamples_Arrays(int k)
	{
		String[] strings = getLastKSamples(k);
	
		double[][] samplesArray = new double[k][ArchSpec.NUM_SOCKETS*ArchSpec.NUM_STATS_PER_SOCKET];
		for (int i = 0; i < strings.length; i++) {
			String energyString = strings[i];
			samplesArray[i] = Utils.stringToPrimitiveSample(energyString);
		}

		return samplesArray;
	}

	public boolean isRunning() {
		return isRunning;
	}

	private static void sleepPrint(int ms) throws InterruptedException {
		int sec = (int)ms/1000;
		ms = ms%1000;
		for (int s = 0; s < sec; s++) {
			System.out.println(s+"/"+(sec+ms));
			Thread.sleep(1000);
		} Thread.sleep(ms);
	}
	
	public String toString()
	{
		String s = "";
		s += "samplingRate: " + getSamplingRate() + " milliseconds\n";
		s += "lifetime: " + Long.toString(getLifetime().toMillis()) + " milliseconds\n";
		s += "number of samples: " + Integer.toString(getNumSamples()) + "\n";

		return s;
	}

	public static void main(String[] args) throws InterruptedException {
		AsyncEnergyMonitor m = null;
		if (args[0].equalsIgnoreCase("Java")) {
			m = new AsyncEnergyMonitorJavaSide();
		} else if (args[0].equalsIgnoreCase("C")) {
			m = new AsyncEnergyMonitorCSide();
		} else {
			System.out.println("invalid args[0]: "+args[0]);
			System.exit(2);
		}
		m.init();
		m.setSamplingRate(125);

		m.start();
		sleepPrint(2000);
		m.stop();

		System.out.println(m);
		int k = 5;
		System.out.println(Arrays.deepToString(m.getLastKSamples_Arrays(k)));
		System.out.println();
		System.out.println(Arrays.toString(m.getLastKTimestamps(k)));
		System.out.println();
		System.out.println(Arrays.toString(m.getLastKSamples(m.getNumSamples())));
		m.writeToFile("AsyncMonitor-"+args[0]+".tmp");
		m.reset();
		m.dealloc();
	}
}
