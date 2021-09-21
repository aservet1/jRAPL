package jRAPL;

import java.time.Instant;
import java.time.Duration;

// TODO : not necessarily for this sample. but this is just a thought. consider having a source of truth for the C side and a source of truth for the Java side.
//			I know you wanted to have a single source of truth, but it might actually be a detriment if it requires Java programmers searching for that truth to
//			have to dig through native code, especially if they're only Java developers. Consider maybe just enforcing the consistency between the parallel C and Java
//			versions in your code base with what you write down instead and keep in mind that you might need to change it in the other world if you make changes
//			in this world, so you don't need what seem like over engineered native access methods to do simple stuff like return the CSV column header or something.
//			Just a thought. Consider it while you go do the rest of your stuff.

public final class EnergyDiff extends EnergySample {

	private Instant startTimestamp;
	private Instant stopTimestamp;

	public EnergyDiff(double[] primitiveSample, Instant startTimestamp, Instant stopTimestamp) {
		super(primitiveSample);
		this.startTimestamp = startTimestamp;
		this.stopTimestamp = stopTimestamp;
	}

	public Duration getTimeElapsed() {
		return Duration.between(startTimestamp, stopTimestamp);
	}

	public Instant getStartTimestamp() {
		return this.startTimestamp;
	}

	public Instant getStopTimestamp() {
		return this.stopTimestamp;
	}

	public static String csvHeader() {
		return EnergySample
			.csvHeader()
				.replace (
					"timestamp",
					"start_timestamp,time_elapsed" // @TODO: maybe set up a JNI route now that you have `energy_diff_csv_header` defined in the native code side?
				);
	}

	@Override
	public String csv() {
		return super.csv() + Long.toString(Utils.timestampToUsec(getStartTimestamp())) + "," + Long.toString(Utils.durationToUsec(getTimeElapsed()));
	}

	public static EnergyDiff between(EnergyStats before, EnergyStats after) {
		double[] primitiveSample =
			EnergyMonitor
				.subtractPrimitiveSamples (
					after.getPrimitiveSample(),
					before.getPrimitiveSample()
				);
		return new EnergyDiff (
			primitiveSample,
			before.getTimestamp(),
			after.getTimestamp()
		);
	}

}
