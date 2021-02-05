package jRAPL;

import java.time.Instant;

/** High-level representation of jrapl's energy stats. */
public final class EnergyStats extends EnergySample
{	
	public EnergyStats() {
		super(Utils.stringToPrimitiveSample(EnergyMonitor.energyStatCheck()));
	}
	
	public EnergyStats(double[] primitiveSample, Instant ts) {
		super(primitiveSample, ts);
	}

	public EnergyStats(double[] primitiveSample) {
		super(primitiveSample);
	}

	@Override
	public String dump() {
		return super.dump();
	}

	@Override
	public String toString() {
		return super.toString();
	}

}