// This is just to test misc random features
package jRAPL;

import java.time.Duration;
import java.time.Instant;

public class DriverTmp {

	public native static void ctimeStart();
	public native static void ctimeStop();
	public native static long ctimeElapsedUsec();

	static double mean(long[] list) {
		long sum = 0;
		for (long n : list) sum += n;
		return sum/list.length;
	}
	static double stdev(long[] list) {
		double m = mean(list);
		double sum = 0;
		for (long n : list) sum += (n-m)*(n-m);
		return Math.sqrt(sum/(list.length-1));
	}

	public static void main(String[] args) throws InterruptedException{
		final int N = 10000;
		long[] ctime = new long[N];
		long[] jtime = new long[N];

		EnergyManager m = new EnergyManager();
		m.activate();
		for (int i = 0; i < N; i++) {
			ctimeStart();
			Thread.sleep(1);
			ctimeStop();
			ctime[i] = ctimeElapsedUsec()-1000;
		}
		System.out.println("c: " + mean(ctime) + " +/- " + stdev(ctime));
		System.out.println("\n\n");
		Instant start, stop;
		for (int i = 0; i < N; i++) {
			start = Instant.now();
			Thread.sleep(1);
			stop = Instant.now();
			jtime[i] = Utils.durationToUsec(Duration.between(start, stop))-1000;
		}
		System.out.println("j: " + mean(jtime) + " +/- " + stdev(jtime));
		m.deactivate();
	}
}