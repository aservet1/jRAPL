package jRAPL;

import java.time.Instant;

/** High-level representation of jrapl's energy stats. */
public final class EnergyStats extends EnergySample
{	// public EnergyStats() {
	// 	String statCheck = EnergyMonitor.energyStatCheck(0); // @TODO make sure the C level thing can just go back to 0 arguments and always reads all sockets
	// 	double[] primitiveSample = Utils.stringToPrimitiveArray(statCheck);
	// 	super(primitiveSample);
	// }
	
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