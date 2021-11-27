import java.time.Instant;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;

public class AsyncFrequencyMonitor implements Runnable {
	private ArrayList<long[]> samples = new ArrayList<long[]>();
	private ArrayList<Instant> timestamps = new ArrayList<Instant>();

	private int samplingRate = 10;
	private volatile boolean exit = false;
	private Thread t;

	private Instant birth;
	private Instant death;
	private Duration lifetime = null;

	private static int cpu_count = Runtime.getRuntime().availableProcessors();

	public void setSamplingRate(int s) {
		samplingRate = s;
	}

	public int getSamplingRate() {
		return samplingRate;
	}

	public long[] getFreqAllCores() {
		long[] freqs = new long[cpu_count];
		try {
			for (int cpu = 0; cpu < cpu_count; cpu++) {
				freqs[cpu] = Integer.parseInt(
					Files.readString(
						Paths.get(
							String.format("/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq",cpu)
						)
					).split("\n")[0]
				);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return freqs;
	}

	public void run() {
		while(!exit) {
			samples.add(getFreqAllCores());
			timestamps.add(Instant.now());
			try {
				Thread.sleep(samplingRate);
			} catch (Exception e) { }
		}
	}

	public void start() {
		birth = Instant.now();
		t = new Thread(this);
		t.start();
	}

	public void stop() {
		exit = true;
		try {
			 t.join();
		} catch (Exception e) {
			System.out.println("Exception " + e + " caught.");
			e.printStackTrace();
		}
		t = null;
		death = Instant.now();
		lifetime = Duration.between(birth, death);
	}

	public Duration getLifetime() {
		return lifetime;
	}

	public void reset() {
		exit = false;
		samples.clear();
	}

	private String stringifySamples() {
		String s = "[";
		int i; for (i = 0; i < samples.size()-1; i++) {
			s += Arrays.toString(samples.get(i)) + ",";
		} s += Arrays.toString(samples.get(i)) + "]";
		return s;
	}

	private long[] timestampsToMillis() {
		long[] millis = new long[timestamps.size()];
		for (int i = 0; i < timestamps.size(); i++) {
			millis[i] = timestamps.get(i).toEpochMilli();
		}
		return millis;
	}

	public void writeFileCSV(String fileName) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter ( // write to stdout if filename is null
				(fileName == null)
					? new OutputStreamWriter(System.out)
					: new FileWriter(new File(fileName))
			);

			String header = "timestamp,";
			int cpu; for (cpu = 0; cpu < cpu_count-1; cpu++) {
				header += String.format("cpu%d,",cpu);
			} header += String.format("cpu%d\n",cpu);
			writer.write(header);

			long firstTime = timestamps.get(0).toEpochMilli();
			assert(samples.size() == timestamps.size());
			for(int i = 0; i < samples.size(); i++) {
				String line = String.format("%d,", timestamps.get(i).toEpochMilli() - firstTime);
				long[] freqs = samples.get(i);
				int j; for (j = 0; j < freqs.length-1; j++) {
					line += String.format("%d,",freqs[j]);
				} line += String.format("%d\n",freqs[j]);
				writer.write(line);
			}
			writer.flush();
			if (fileName != null) writer.close();
		} catch (IOException e) {
			System.out.println("error writing " + fileName);
			e.printStackTrace();
		}
	}
}
