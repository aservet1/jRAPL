import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Jtimer
{
	static long instToUsec(Instant timestamp) {
		return ChronoUnit.MICROS.between(Instant.EPOCH, timestamp);
	}

	static long usecs(int msec) {
		Instant bef, aft;
		bef = Instant.now();
		try {
			Thread.sleep(msec);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		aft = Instant.now();
		return instToUsec(aft) - instToUsec(bef);
	}

	static void go(int s, int trials){
		long[] buf = new long[trials];
	
		int warmups = 100;
	    for (int w = 0; w < warmups; w++) {
			buf[w%trials] = usecs(s);
		}
		for (int i = 0; i < trials; i++) {
			buf[i] = usecs(s);
		}
		System.out.printf("["); for (int i = 0; i < trials-1; i++) {
			System.out.printf("%d,",buf[i]);
		} System.out.printf("%d]",buf[trials-1]);
	}

	public static void main(String[] args)
	{
		if (args.length != 2) {
			System.out.printf("usage: java Jtimer <msecs> <number of trials>\n");
			System.exit(1017);
		}
		int s = Integer.parseInt(args[0]);
		int trials = Integer.parseInt(args[1]);

		go(s,trials);
	}
}
