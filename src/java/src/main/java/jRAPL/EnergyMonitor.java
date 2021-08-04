package jRAPL;

import java.util.Arrays;
import java.time.Instant;
import java.time.Duration;

public class EnergyMonitor extends EnergyManager {

	@Override
	public void activate() { super.activate(); }
	@Override
	public void deactivate() { super.deactivate(); }

	/** Package private so it can be called in JMH things.
	 *	@TODO consider making this public in and of itself,
	 *	so people can directly get a CSV reading if thats
	 *	all theyre going to need. or have a SyncEnergyMonitor
	 *	method that wraps this like getStringSample()
	*/ native static String energyStatCheck(); 

	private static double[] stringArrayToDoubleArray(String[] s) {
		double[] d = new double[s.length];
		for (int i = 0; i < s.length; i++)
			d[i] = Double.parseDouble(s[i]);
		return d;
	}

	//@TODO consider if you want the timestamp to be part of the primitive sample. let's say no for now
    protected static double[] stringToPrimitiveSample(String energyString) {
		String[] parts = energyString.split(",");
		return stringArrayToDoubleArray (
			Arrays.copyOf(parts, parts.length-1)
		);
	}

	protected static EnergyStats stringToEnergyStats(String energyString) {
		String[] parts = energyString.split(",");
		return new EnergyStats (
			stringArrayToDoubleArray (
				Arrays.copyOf(parts, parts.length-1)
			),
			Utils.usecToInstant (
				Long.parseLong(parts[parts.length-1])
			)
		);
    }

	// wondering if this is necessary to have, might as well let the user do their own logic for this if theyre taking on the primitive samples already    
	protected static double[] subtractPrimitiveSamples(double[] a, double[] b) {
		assert ( a.length == b.length );
		double[] diff = new double[a.length];
		for (int i = 0; i < diff.length; i++) {
			diff[i] = a[i] - b[i];
			if (diff[i] < 0) diff[i] += ArchSpec.RAPL_WRAPAROUND;
		}
		return diff;
    }

}
