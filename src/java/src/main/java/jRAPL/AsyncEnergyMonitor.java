
package jRAPL;

import java.time.Instant;
import java.time.Duration;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.IOException;

import java.util.Arrays; // just for the test driver, and debug. should eventually not actually be used in user-facing code, if we can make that happen. for modularity and cleanliness :)

public abstract class AsyncEnergyMonitor extends EnergyMonitor {

	protected Instant monitorStartTime = null;
	protected Instant monitorStopTime = null;
	protected boolean isRunning = false;
	protected int samplingRate;

	@Override
	public  void  activate()
	{  super.activate();   }
	
	@Override
	public void deactivate()
	{  super.deactivate(); }

	/** Gets the number of samples the monitor currently collected
	 *	@return number of samples collected so far
	*/
	public abstract int getNumSamples();
	/** Sets the energy sampling rate
	 *	@param s sampling rate (in milliseconds)
	*/
	public abstract void setSamplingRate(int s);
	public abstract int getSamplingRate();

	public Duration getLifetime() {
		if (monitorStartTime != null && monitorStopTime != null)
			return Duration.between(monitorStartTime, monitorStopTime);
		else return null;
	}

	/** Starts monitoring in background thread until 
	 *	main thread calls <code>this.stop()</code>.
	*/
	public void start() {
		isRunning = true;
		monitorStartTime = Instant.now();
	}

	/** Stops monitoring and storing energy samples. */
	public void stop() {
		monitorStopTime = Instant.now();
		isRunning = false;
	}

	/** Resets the object for reuse. */
	public void reset() {
		monitorStartTime = null;
		monitorStopTime = null;
	}

	/** Last K timestamps */
	public abstract Instant[] getLastKTimestamps(int k);
	/** Last K samples in raw string format */
	public abstract String[] getLastKSamples(int k);
	/** Last K samples as EnergyStats objects  */
	public EnergyStats[] getLastKSamples_Objects(int k) {
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
	/** Last K samples as primitive arrays of doubles. You can use this in conjunction with getLastKTimestamps() if you want parralell arrays. */
	public double[][] getLastKSamples_Arrays(int k) {
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

	/** Dumps all samples to file, along with the sampling rate, in CSV format.
	 *	Same format as <code>this.toString()</code>
	 *	@param fileName name of file to write to
	*/
	public abstract void writeFileCSV(String fileName);

	public void writeFileMetadata(String fileName) { //@TODO at some point, make it write to stdout instead of a file if fileName == null
		// currently enforcing only JSON files
		if (fileName != null) {
			String[] parts = fileName.split("\\.");
			if (parts.length > 0 && !(parts[parts.length-1].equalsIgnoreCase("json"))) {
				System.err.printf("<<<<<<<<<<<< ERROR: writeToFileMetaInfo() only acccepts .json output file format, not the received: %s\n", fileName);
				System.err.println(Thread.currentThread().getStackTrace());
				System.exit(2);
			}
		}
		
		long samplingRate = getSamplingRate();
		long lifetime = getLifetime().toMillis();
		long numSamples = getNumSamples();

		// java generate JSON for the meta info object RIGHT HERE RIGHT NOW
		String json = String.format( //@TODO make sure all of the metainfo points are here or if you need to gather more data to display
				"{\"samplingRate\": %d, \"lifetime\": %d, \"numSamples\": %d, \"energyWrapAround\": %f }",
				samplingRate, lifetime, numSamples, (float)ArchSpec.RAPL_WRAPAROUND); //TODO make sure wraparound gets accurately calculated on the C side before you start using this
		try {
			BufferedWriter writer = new BufferedWriter (
							(fileName == null)
								? new OutputStreamWriter(System.out)
								: new FileWriter(new File(fileName))
							);
			//FileWriter writer = new FileWriter(fileName);
			writer.write(json);
			writer.flush();
			if (fileName != null) writer.close();
		} catch (IOException ex) {
			System.err.printf("error in writeToFileMetaInfo(%s)\n",fileName);
			ex.printStackTrace();
		}
	}

	public String toString() {

		int samplingRate = getSamplingRate();
		long lifetime = getLifetime().toMillis();
		int numSamples = getNumSamples();

		String s = String.join("\n",
			"samplingRate: " + samplingRate + " milliseconds",
			"lifetime: " + lifetime + " milliseconds",
			"numSamples: " + numSamples
		);

		return s;
	}

	// this is just used in the main() driver, not part of the AsyncMonitor
	private static void sleepPrint(int ms) throws InterruptedException {
		int sec = (int)ms/1000;
		ms = ms%1000;
		for (int s = 0; s < sec; s++) {
			System.out.printf("%d/%d\n",s,(sec+ms));
			Thread.sleep(1000);
		} Thread.sleep(ms);
	}

	public static void main(String[] args) throws InterruptedException {
		AsyncEnergyMonitor m = null;
		if (args[0].equalsIgnoreCase("Java")) {
			m = new AsyncEnergyMonitorJavaSide();
		} else if (args[0].equalsIgnoreCase("C")) {
			m = new AsyncEnergyMonitorCSide(args[1]);
		} else {
			System.out.println("invalid args[0]: "+args[0]);
			System.exit(2);
		}
		m.activate();
		m.setSamplingRate(12);

		m.start();
		sleepPrint(3000);
		m.stop();

		System.out.println(m);
		int k = 5;
		System.out.println(Arrays.deepToString(m.getLastKSamples_Arrays(k)));
		System.out.println();
		System.out.println(Arrays.toString(m.getLastKTimestamps(k)));
		System.out.println();
		System.out.println(Arrays.toString(m.getLastKSamples(m.getNumSamples())));

		String name = args[0] + ((args.length == 1) ? "" : args[1]);

		m.writeFileMetadata("AsyncMonitor-"+name+"-metainfo.json");
		m.writeFileCSV("AsyncMonitor-"+name+".csv");

		m.reset();
		m.deactivate();
	}
}
