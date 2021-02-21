
package jRAPL;

import java.time.Instant;
import java.time.Duration;

import java.util.Arrays; // just for the test driver, not actually used in the code

public abstract class AsyncEnergyMonitor extends EnergyMonitor {

	protected Instant monitorStartTime = null;
	protected Instant monitorStopTime = null;
	protected boolean isRunning = false;
	protected int samplingRate;

	@Override
	public void activate() { super.activate(); }
	
	@Override
	public void deactivate() { super.deactivate(); }

	/** Dumps all samples to file, along with the sampling rate, in CSV format.
	 *	Same format as <code>this.toString()</code>
	 *	@param fileName name of file to write to
	*/
	public abstract void writeToFile(String fileName);

	/** Gets the number of samples the monitor currently collected
	 *	@return number of samples collected so far
	*/
	public abstract int getNumSamples();
	/** Sets the energy sampling rate
	 *	@param s sampling rate (in milliseconds)
	*/
	public abstract void setSamplingRate(int s);
	public abstract int getSamplingRate();

	public Duration getLifetime()
	{
		if (monitorStartTime != null && monitorStopTime != null)
			return Duration.between(monitorStartTime, monitorStopTime);
		else return null;
	}

	/** Starts monitoring in background thread until 
	 *	main thread calls <code>this.stop()</code>.
	*/
	public void start()
	{
		isRunning = true;
		monitorStartTime = Instant.now();
	}

	/** Stops monitoring and storing energy samples. */
	public void stop()
	{
		monitorStopTime = Instant.now();
		isRunning = false;
	}

	/** Resets the object for reuse. */
	public void reset()
	{
		monitorStartTime = null;
		monitorStopTime = null;
	}

	/** Last K timestamps */
	public abstract Instant[] getLastKTimestamps(int k);
	/** Last K samples in raw string format */
	public abstract String[] getLastKSamples(int k);
	/** Last K samples as EnergyStats objects  */
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
	/** Last K samples as primitive arrays of doubles */
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

	public String toString()
	{
		String s = "";
		s += "samplingRate: " + getSamplingRate() + " milliseconds\n";
		s += "lifetime: " + Long.toString(getLifetime().toMillis()) + " milliseconds\n";
		s += "number of samples: " + Integer.toString(getNumSamples()) + "\n";

		return s;
	}

	private static void sleepPrint(int ms) throws InterruptedException {
		int sec = (int)ms/1000;
		ms = ms%1000;
		for (int s = 0; s < sec; s++) {
			System.out.println(s+"/"+(sec+ms));
			Thread.sleep(1000);
		} Thread.sleep(ms);
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
		m.activate();
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
		m.deactivate();
	}
}
