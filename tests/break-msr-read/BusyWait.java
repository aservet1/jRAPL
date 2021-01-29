import java.time.Instant;
import java.time.Duration;
public class BusyWait
{
	static void busyWait(long n) {
		long i = 0;
		while (i < n) {
			i++;
		}
	}

	public static void main(String[] args)
	{
		long n = Long.parseLong(args[0]);
		Instant start = Instant.now();
		busyWait(n);
		Duration time = Duration.between(start, Instant.now());
		System.out.println(time);
	}
}
