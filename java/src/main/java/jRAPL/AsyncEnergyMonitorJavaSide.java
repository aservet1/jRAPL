package jRAPL;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileWriter;

import java.time.Instant;
import java.time.Duration;

public class AsyncEnergyMonitorJavaSide extends AsyncEnergyMonitor implements Runnable
{
	private ArrayList<String> samples;
	private int samplingRate; // milliseconds
	private volatile boolean exit = false;
	private Thread t = null;

	protected final ArrayList<Instant> timestamps;

	public AsyncEnergyMonitorJavaSide() {
		samplingRate = 10;
		timestamps = new ArrayList<Instant>();
		samples = new ArrayList<String>();
	}

	public AsyncEnergyMonitorJavaSide(int s) {
		samplingRate = s;
		timestamps = new ArrayList<Instant>();
		samples = new ArrayList<String>();
	}

	@Override
	public void activate() {
		super.activate();
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
	}

	/** Overrides the Runnable interface's run() method.
	 * Dont call this on its own, but it's gotta be public
	 * for interface override reasons.
	*/	
	public void run() {
		while (!exit) {
			String energyString = EnergyMonitor.energyStatCheck();
			samples.add(energyString);
			timestamps.add(Instant.now());
			try { Thread.sleep(samplingRate); } catch (Exception e) {}
		}
	}

	@Override
	public void start() {
		super.start();
		t = new Thread(this);
		t.start();
	}

	@Override
	public void stop() {
		super.stop();
		exit = true;
		try {
			 t.join();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		t = null;
	}

	@Override
	public void reset() {
		super.reset();
		exit = false;
		samples.clear();
		timestamps.clear();
	}

	@Override
	public String[] getLastKSamples(int k) {
		int start = samples.size() - k;
		int arrayIndex = 0;

		if (start < 0) {
			start = 0;
			k = samples.size();
		}
		
		String[] samplesArray = new String[k];
		for (int i = start; i < samples.size(); i++)
			samplesArray[arrayIndex++] = samples.get(i);
		
		return samplesArray;
	}

	@Override
	public Instant[] getLastKTimestamps(int k) {
		int start = timestamps.size() - k;
		int arrayIndex = 0;
		if (start < 0) {
			start = 0;
			k = timestamps.size();
		}

		Instant[] timestampsArray = new Instant[k];

		for (int i = start; i < timestamps.size(); i++)
			timestampsArray[arrayIndex++] = timestamps.get(i);

		return timestampsArray;

	}

	@Override
	public int getSamplingRate() {
		return samplingRate;
	}

	@Override
	public void setSamplingRate(int s) {
		samplingRate = s;
	}

	@Override
	public int getNumSamples() {
		return samples.size();
	}

	@Override
	public void writeFileCSV(String fileName) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter ( // write to stdout if filename is null
									(fileName == null)
										? new OutputStreamWriter(System.out)
										: new FileWriter(new File(fileName))
									);
			writer.write("socket,"+ArchSpec.ENERGY_STATS_STRING_FORMAT.split("@")[0]+",timestamp\n");
			for (int i = 0; i < samples.size(); i++) {
				String energyString = samples.get(i);
				String[] perSocketStrings = energyString.split("@");
				long usecs = Utils.timestampToUsec(timestamps.get(i));
				for (int socket = 1; socket <= perSocketStrings.length; socket++) {
					writer.write (
						String.format("%d,%s,%d\n", socket, perSocketStrings[socket-1], usecs)
					);
				}
			}
			writer.flush();
			if (fileName != null) writer.close();
		} catch (IOException e) {
			System.out.println("error writing " + fileName);
			e.printStackTrace();
		}
	}

}
