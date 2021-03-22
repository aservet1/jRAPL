
import java.time.Instant;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.io.*;

public class AsyncMemoryMonitor implements Runnable {
	private ArrayList<Long> samples;
	private int samplingRate = 10;
	private volatile boolean exit = false;
	private static final Runtime runtime = Runtime.getRuntime();
	private Thread t;

	private Instant birth;
	private Instant death;
	private Duration lifetime = null;

	public AsyncMemoryMonitor() {
		samples = new ArrayList<Long>();
	}

	public void setSamplingRate(int s) {
		samplingRate = s;
	}

	public int getSamplingRate() {
		return samplingRate;
	}

	private long memoryUsed() {
		return runtime.totalMemory() - runtime.freeMemory();
	}

	public void run() {
		while(!exit) {
			samples.add(memoryUsed());
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

	public Duration getLifetime()
	{
		return lifetime;
	}

	private static long durationToUsec(Duration duration) {
		Instant i = Instant.ofEpochMilli(1000000); // arbitrary Instant point
		Instant isubbed = i.minus(duration);
		return ChronoUnit.MICROS.between(isubbed, i);
	}

	public void reset() {
		exit = false;
		samples.clear();
	}	

	public double average() {
		if (samples.size() == 0) return 0;
		double sum = 0;
		for (Long x : this.samples)
			sum += x;
		return sum / samples.size();
	}

	public double stdev() {
		if (samples.size() == 0) return 0;
		double s = 0;
		double avg = this.average();
		for (Long x : this.samples)
			s += Math.pow(x - avg, 2);
		s = Math.sqrt(s/samples.size());
		return s;
	}

	public String toString() { 
		String s = new String();
		int size = samples.size();
		if (size != 0) {
			for (int i = 0; i < size-1; i++) {
				if ( i%10 == 0 ) s += '\n';
				s += samples.get(i)+",";
			}
			s += samples.get(size-1)+"\n";
		} else {
			s += "< no samples read >\n";
		}
		s += "avg: " + this.average() + "\nstd: " + this.stdev() + "\n";
		s += "lifetime: " + this.lifetime.toMillis() + " msec";
		return s;
	}

	public void writeFile(String fileName) { // JSON
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter ( // write to stdout if filename is null
									(fileName == null)
										? new OutputStreamWriter(System.out)
										: new FileWriter(new File(fileName))
									);
			writer.write("{\"samples\":[");
			for (int i = 0; i < samples.size()-1; i++) {
				writer.write(String.format("%d,", samples.get(i)));
			} writer.write(Long.toString(samples.get(samples.size()-1)));
			writer.write(String.format("],\"lifetime\":%d,\"numSamples\":%d, \"samplingRate\": %d }",
										durationToUsec(getLifetime()),samples.size(),samplingRate)); //@TODO maybe have a less ugly, more modular way of generating the JSON string
			writer.flush();
			if (fileName != null) writer.close();
		} catch (IOException e) {
			System.out.println("error writing " + fileName);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		AsyncMemoryMonitor m = new AsyncMemoryMonitor();
		m.setSamplingRate(10);

		m.start();
		Thread.sleep(1000);
		m.stop();

		m.writeFile(null);
	}

}




