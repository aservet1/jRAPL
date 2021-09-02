
import java.time.Instant;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.io.*;

public class AsyncMemoryMonitor implements Runnable {
	private ArrayList<Long> samples = new ArrayList<Long>();
	private ArrayList<Instant> timestamps = new ArrayList<Instant>();

	private ArrayList<Long> samples_negative_caught = new ArrayList<Long>();
	private ArrayList<Instant> timestamps_negative_caught = new ArrayList<Instant>();

	private int samplingRate = 10;
	private volatile boolean exit = false;
	private static final Runtime runtime = Runtime.getRuntime();
	private Thread t;

	private Instant birth;
	private Instant death;
	private Duration lifetime = null;

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
		long memory;
		Instant timestamp;
		Instant now;
		while(!exit) {
			memory = memoryUsed();
			timestamp = Instant.now();
			if (memory < 0) { //@TODO: for some whacky reason, we get a negative value occasionally.
						  //this happened about 90 out of the last 9374973 or so times we ran
						  // it, so we're just going to discard them. however, might be good to
						  // figure out why that's happening and if it means the monitoring method
						  // is untrustworthy
				samples_negative_caught.add(memory);
				timestamps_negative_caught.add(timestamp);
			} else {
				samples.add(memory);
				timestamps.add(timestamp);
			}
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

	private ArrayList<Long> instantsToMillis(ArrayList<Instant> instants) {
		ArrayList<Long> ts = new ArrayList<>();
		long bias = 0;//instants.get(0).toEpochMilli();
		for (Instant i : instants)
			ts.add(i.toEpochMilli() - bias);
		return ts;
	}

	public void writeFile(String fileName) { // JSON
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter ( // write to stdout if filename is null
									(fileName == null)
										? new OutputStreamWriter(System.out)
										: new FileWriter(new File(fileName))
									);
			writer.write (
				String.format (
					 "{"
					+	"\"samples\":%s, "
					+	"\"timestamps\":%s, "
					+	"\"lifetime\":%d,"
					+	"\"numSamples\":%d, "
					+	"\"samplingRate\": %d, "
					+	"\"negativesCaught\":{ "
					+		"\"samples\": %s, "
					+		"\"timestamps\": %s "
					+	"}"
					+"}",
					samples.toString(),
					instantsToMillis(timestamps).toString(),
					durationToUsec(getLifetime()),
					samples.size(),
					samplingRate,
					samples_negative_caught.toString(),
					instantsToMillis(timestamps_negative_caught).toString()
				)
			);

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

