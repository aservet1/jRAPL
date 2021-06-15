package jRAPL;

import java.time.Duration;

public final class EnergyDiff extends EnergySample {

	private Duration elapsedTime = null; //time between the two EnergyStamps

	public EnergyDiff(double[] primitiveSample, Duration elapsedTime) {
		super(primitiveSample);
		this.elapsedTime = elapsedTime;
	}

	public void setElapsedTime(Duration elapsed) {
		elapsedTime = elapsed;
	}

	public Duration getElapsedTime() {
		return this.elapsedTime;
	}

	public static String csvHeader() {
		return EnergySample.csvHeader() + "elapsedTime";
	}

	@Override
	public String csv() {
		return super.csv() + (
					(this.elapsedTime == null)
					? ("null") : (Long.toString(Utils.durationToUsec(elapsedTime)))
				);
	}

	public static EnergyDiff between(EnergyStats before, EnergyStats after) {
		double[] primitiveSample = Utils.subtractPrimitiveSamples(after.getPrimitiveSample(), before.getPrimitiveSample());
		Duration elapsedTime = (before.getTimestamp() == null || after.getTimestamp() == null)
				? null : Duration.between(before.getTimestamp(), after.getTimestamp());
		return new EnergyDiff(primitiveSample, elapsedTime);
	}

	// @Override
	// public String toString() {
	// 	return String.join(", ", super.toString(), "Duration (nanoseconds): " + this.elapsedTime.toNanos());
	// }
}
