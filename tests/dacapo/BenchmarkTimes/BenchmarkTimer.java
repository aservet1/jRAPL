
import org.dacapo.harness.CommandLineArgs;   
import org.dacapo.harness.Callback;      

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.time.Duration;

/** Runs a benchmark once and appends it to the log file. This isn't meant to be anything
	precise, since it's only one shot with no warmups. But it'll be a general idea of how long
	they are, like 1 second as opposed to 30. */

public class BenchmarkTimer extends Callback {
	
	private Instant startStamp, stopStamp;

	public static long durationToMillis(Duration duration) {
		Instant i = Instant.ofEpochMilli(1000000); // arbitrary Instant point
		Instant isubbed = i.minus(duration);
		return ChronoUnit.MILLIS.between(isubbed, i);
	}

	public BenchmarkTimer(CommandLineArgs args) {
		super(args);
	}

	@Override
	public void start(String benchmark) {
		super.start(benchmark);
		startStamp = Instant.now();
	}

	@Override
	public void stop(long l, boolean w) {
		super.stop(l, w);
		stopStamp = Instant.now();
	}

	@Override
	public void complete(String benchmark, boolean valid) {
		super.complete(benchmark, valid);

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("benchmark-times.log", true));
			writer.write(
				String.format("%s: %d msec", benchmark,
					durationToMillis(Duration.between(startStamp,stopStamp)) ));
			writer.newLine();
			writer.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally { 
			if (writer != null)
				try {
					writer.close();
				} catch (IOException ioe2) { }
      	}

	}
}
