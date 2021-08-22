package jRAPL;

import java.time.Duration;

// @TODO replace elapsedTime with startStamp and stopStamp (two instants instead of a duration, but can have a getter method to return the difference if you want)
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
		return EnergySample
				.csvHeader()
					.replace (
						"timestamp",
						"elapsedTime"
					);
	}

	@Override
	public String csv() {
		return super.csv() + (
			(this.elapsedTime == null)
				? ("null") // TODO ugh, do I really want null timestamps? i dont think that's necessary any more. it might be though
				: Long.toString (
					Utils.durationToUsec(elapsedTime)
				)
		);
	}

	public static EnergyDiff between(EnergyStats before, EnergyStats after) {
		double[] primitiveSample =
			EnergyMonitor
				.subtractPrimitiveSamples (
					after.getPrimitiveSample(),
					before.getPrimitiveSample()
			);
		Duration elapsedTime = ( before.getTimestamp() == null || after.getTimestamp() == null )
			? null // TODO ugh, do I really want null timestamps? i dont think that's necessary any more. it might be though
			: Duration.between (
				before.getTimestamp(),
				after.getTimestamp()
			);
		return new EnergyDiff (
			primitiveSample,
			elapsedTime
		);
	}

}
