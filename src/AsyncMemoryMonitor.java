
package jrapltesting;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import jrapl.*;

//@TODO consider making an abstract AsyncMonitor class, unless you
// think it would be too clunky and that you wouldn't need more than
// this and the C side one, in which case it would be confusing, also
// you may or may not even release the MemoryMonitor to JRAPL's final
// product anyway
public class AsyncMemoryMonitor implements Runnable,AsyncMonitor {
	private ArrayList<Long> samples;
	private int samplingRate = 10;
	private volatile boolean exit = false;
	private static final Runtime runtime = Runtime.getRuntime();
	private Thread t;

	private Instant birth;
	private Instant death;
	private Duration lifetime;

	public AsyncMemoryMonitor() {
		samples = new ArrayList<Long>();
	}

	/*private long memoryDiff()
	{
		long before = runtime.totalMemory() - runtime.freeMemory();
		try { Thread.sleep(samplingRate); }
		catch (Exception e) { System.out.println("thread sleep error in AsyncMemoryMonitor"); }
		long after = runtime.totalMemory() - runtime.freeMemory();
		return after - before;
	}*/

	private long memoryUsed() {
		return runtime.totalMemory() - runtime.freeMemory();
	}

	public void run() {
		while(!exit) {
			samples.add(memoryUsed());
			if (!exit) try {
				Thread.sleep(samplingRate);
			} catch (Exception e) { }
			//System.out.print(".");
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

	public void reInit() {
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
}

