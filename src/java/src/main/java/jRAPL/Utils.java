/** Miscellaneous helper methods */

package jRAPL;

import java.time.Instant;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import java.util.Arrays;

class Utils {

	public static Instant usecToInstant(long usec) {
		return Instant.EPOCH.plus(usec, ChronoUnit.MICROS);
	}
	public static long timestampToUsec(Instant timestamp) {
		return ChronoUnit.MICROS.between(Instant.EPOCH, timestamp);
	}
	public static long durationToUsec(Duration duration) {
		Instant i = Instant.ofEpochMilli(1000000); // arbitrary Instant point
		Instant isubbed = i.minus(duration);
		return ChronoUnit.MICROS.between(isubbed, i);
	}

	//@TODO consider if you want the timestamp to be part of the primitive sample. let's say no for now
    public static double[] stringToPrimitiveSample(String energyString) {
		String[] stringVals = energyString.split(",");
		return stringArrayToDoubleArray(
			Arrays.copyOfRange(stringVals,0,stringVals.length) // removes the last item
		);
	}

	private static double[] stringArrayToDoubleArray(String[] s) {
		double[] d = new double[s.length];
		for (int i = 0; i < s.length; i++)
			d[i] = Double.parseDouble(s[i]);
		return d;
	}

	public static EnergyStats stringToEnergyStats(String energyString) {
		String[] parts = energyString.split(",");
		String timestampString = parts[parts.length-1];
		long usecs = Long.parseLong(timestampString);
		Instant ts = usecToInstant(usecs);
		return new EnergyStats(stringToPrimitiveSample(energyString), ts);
		//TODO this is readundant, kind of
    }
    
	public static double[] subtractPrimitiveSamples(double[] a, double[] b) {
		assert ( a.length == b.length );
		double[] diff = new double[a.length];
		for (int i = 0; i < diff.length; i++) {
			diff[i] = a[i] - b[i];
			if (diff[i] < 0)
				diff[i] += ArchSpec.RAPL_WRAPAROUND;
		}
		return diff;
    }
    
    public static String csvPrimitiveArray(double[] a) {
		String s = new String();
		int i; for (i = 0; i < ArchSpec.NUM_SOCKETS*ArchSpec.NUM_STATS_PER_SOCKET; i++) {
			s += String.format("%.6f",a[i]) + ",";
		} s += String.format("%.6f",a[i]);
		return s;
	}

	public static String csvPrimitiveArray(double[] a, Instant timestamp) {
		return String.format("%s,%d", csvPrimitiveArray(a), timestampToUsec(timestamp));
	}

	public static String csvPrimitiveArray(double[] a, Duration elapsedTime) {
		return String.format("%s,%d", csvPrimitiveArray(a), durationToUsec(elapsedTime));
	}
}
