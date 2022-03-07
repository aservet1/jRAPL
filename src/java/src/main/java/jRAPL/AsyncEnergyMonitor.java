package jRAPL;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileWriter;

import java.time.Instant;
import java.time.Duration;

public class AsyncEnergyMonitor extends EnergyMonitor implements Runnable {

	private Instant monitorStartTime = null;
	private Instant monitorStopTime = null;
	private boolean isRunning = false;

	private ArrayList<EnergyMeasurement> measurements;
	private int samplingRate; // milliseconds
	private volatile boolean exit = false;
	private Thread t = null;

	public AsyncEnergyMonitor() {
		samplingRate = 10;
		measurements = new ArrayList<EnergyMeasurement>();
	}

	public AsyncEnergyMonitor(int s) {
		samplingRate = s;
		measurements = new ArrayList<EnergyMeasurement>();
	}

	@Override
	public void activate() {
		super.activate();
	}

	@Override
	public void deactivate() {
		super.deactivate();
	}

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
		t = new Thread(this);
		t.start();
	}

	/** Stops monitoring and storing energy measurements */
	public void stop() {
		monitorStopTime = Instant.now();
		isRunning = false;
		exit = true;
		try {
		    t.join();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		t = null;
	}

	/** Resets the object for reuse. */
	public void reset() {
		monitorStartTime = null;
		monitorStopTime = null;
        isRunning = false;
		exit = false;
		measurements.clear();
	}

	public boolean isRunning() {
		return isRunning;
	}

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
		long numMeasurements = getNumMeasurements();

		// java generate JSON for the meta info object RIGHT HERE RIGHT NOW
		String json = String.format( //@TODO make sure all of the metainfo points are here or if you need to gather more data to display
				"{\"samplingRate\": %d, \"lifetime\": %d, \"numMeasurements\": %d }",
				samplingRate, lifetime, numMeasurements);
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
		int numMeasurements = getNumMeasurements();

		String s = String.join("\n",
			"samplingRate: " + samplingRate + " milliseconds",
			"lifetime: " + lifetime + " milliseconds",
			"numMeasurements: " + numMeasurements
		);

		return s;
	}

	/** Overrides the Runnable interface's run() method.
	 * Dont call this on its own, but it's gotta be public
	 * for interface override reasons.
	*/	
	public void run() {
		EnergySample before = this.getEnergySample();
		EnergySample after;
		while (!exit) {
			try { Thread.sleep(samplingRate); } catch (Exception e) {  }
			after = this.getEnergySample();
			measurements.add(EnergyMeasurement.between(before, after));
			before = after;
		}
	}

	/** Last K measurements in raw string format */
	public EnergyMeasurement[] getLastKMeasurements(int k) {
		int start = measurements.size() - k;
		int arrayIndex = 0;

		if (start < 0) {
			start = 0;
			k = measurements.size();
		}

		EnergyMeasurement[] lastK = new EnergyMeasurement[k];
		for (int i = start; i < measurements.size(); i++) {
			lastK[arrayIndex++] = measurements.get(i);
        }
		
		return lastK;
	}

	public int getSamplingRate() {
		return samplingRate;
	}

	/** Sets the energy sampling rate
	 *	@param s sampling rate (in milliseconds)
	*/
	public void setSamplingRate(int s) {
		samplingRate = s;
	}

	/** Gets the number of measurements the monitor currently collected
	 *	@return number of measurements collected so far
	*/
	public int getNumMeasurements() {
		return measurements.size();
	}

	/** Dumps all measurements to file, along with the sampling rate, in CSV format.
	 *	Same format as <code>this.toString()</code>
	 *	@param fileName name of file to write to
	*/
	public void writeFileCSV(String fileName) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter (
				(fileName == null)
					? new OutputStreamWriter(System.out)
					: new FileWriter(new File(fileName))
			);
			writer.write(EnergyMeasurement.csvHeader()+"\n");
			for (EnergyMeasurement measurement : measurements)
				writer.write(measurement.csv()+"\n");
			writer.flush();
			if (fileName != null) writer.close();
		} catch (IOException e) {
			System.out.println("error writing " + fileName);
			e.printStackTrace();
		}
	}
}
