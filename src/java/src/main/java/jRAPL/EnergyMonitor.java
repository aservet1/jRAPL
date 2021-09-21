package jRAPL;

import java.util.Arrays;
import java.time.Instant;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class EnergyMonitor extends EnergyManager {

	@Override
	public void activate() { super.activate(); }
	@Override
	public void deactivate() { super.deactivate(); } 

	private static double[] stringArrayToDoubleArray(String[] s) {
		double[] d = new double[s.length];
		for (int i = 0; i < s.length; i++)
			d[i] = Double.parseDouble(s[i]);
		return d;
	}

    protected static double[] statStringToPrimitiveSample(String energyString) {
		String[] parts = energyString.split(",");
		return stringArrayToDoubleArray (
			Arrays.copyOf(parts, parts.length-1)
		);
	}
    protected static double[] diffStringToPrimitiveSample(String energyString) {
		String[] parts = energyString.split(",");
		return stringArrayToDoubleArray (
			Arrays.copyOf(parts, parts.length-2)
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
	protected static EnergyDiff stringToEnergyDiff(String energyString) {
		String[] parts = energyString.split(",");
		Instant startTimestamp = Utils.usecToInstant(
			Long.parseLong(parts[parts.length-2])
		);
		Instant stopTimestamp = startTimestamp.plus(
			Long.parseLong(
				parts[parts.length-1]
			),
			ChronoUnit.MICROS
		);
		return new EnergyDiff (
			stringArrayToDoubleArray (
				Arrays.copyOf(parts, parts.length-2)
			),
			startTimestamp, stopTimestamp
		);
    }

	// wondering if this is necessary to have, might as well let the user do their own logic for this if theyre taking on the primitive samples already    
	protected static double[] subtractPrimitiveSamples(double[] a, double[] b) {
		assert ( a.length == b.length );
		double[] diff = new double[a.length];
		for (int i = 0; i < diff.length; i++) {
			diff[i] = a[i] - b[i];
			if (diff[i] < 0)
				diff[i] +=
					(i % ArchSpec.NUM_STATS_PER_SOCKET == ArchSpec.DRAM_IDX)
						? ArchSpec.DRAM_RAPL_WRAPAROUND
						: ArchSpec.RAPL_WRAPAROUND
					;
		}
		return diff;
    }

}
