
package jRAPL;

import java.time.Duration;

public final class EnergyDiff extends EnergySample
{
	private Duration elapsedTime = null; //time between the two EnergyStamps

	public EnergyDiff(double[] primitiveSample, Duration elapsedTime) {
		super(primitiveSample);
		this.elapsedTime = elapsedTime;
	}

	public Duration getElapsedTime() {
		return this.elapsedTime;
	}

	@Override
	public String dump() {
		return String.join(
			",",
			super.dump(),
			(this.elapsedTime == null)
			? ("null")
			: (Long.toString(this.elapsedTime.toNanos()))
		);
	}

	public static EnergyDiff between(EnergyStats before, EnergyStats after) {
		double[] primitiveSample = Utils.subtractPrimitiveSamples(after.getPrimitiveSample(), before.getPrimitiveSample());
		Duration elapsedTime = (before.timestamp == null || after.timestamp == null)
									? null : Duration.between(before.timestamp, after.timestamp);
		return new EnergyDiff(primitiveSample, elapsedTime);
	}

	@Override
	public String toString() {
		return String.join(", ", super.toString(), "Duration (nanoseconds): " + this.elapsedTime.toNanos());
	}

}