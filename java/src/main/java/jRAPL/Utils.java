/** Miscellaneous helper methods */

package jRAPL;

import java.time.Instant;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

class Utils {

	public static long timestampToUsec(Instant timestamp) {
		return ChronoUnit.MICROS.between(Instant.EPOCH, timestamp);
	}

	public static long durationToUsec(Duration duration) {
		Instant i = Instant.ofEpochMilli(1000000); // arbitrary Instant point
		Instant isubbed = i.minus(duration);
		return ChronoUnit.MICROS.between(isubbed, i);
	}

    public static double[] stringToPrimitiveSample(String energyString) {
		String[] stringVals = energyString.split("@|,");
		double[] stats = new double[stringVals.length];
		for (int i = 0; i < stringVals.length; i++)
			stats[i] = Double.parseDouble(stringVals[i]);

		return stats;
	}

	public static EnergyStats stringToEnergyStats(String energyString) {
		return new EnergyStats(stringToPrimitiveSample(energyString));
    }
    
	public static EnergyStats stringToEnergyStats(String energyString, Instant timestamp) {
		return new EnergyStats(stringToPrimitiveSample(energyString), timestamp);
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
		int i; for (i = 0; i < a.length-1; i++) {
			s += String.format("%.4f",a[i]) + ",";
		} s += String.format("%.4f",a[i]);
		return s;
	}

	public static String csvPrimitiveArray(double[] a, Instant timestamp) {
		return String.format("%s,%d", csvPrimitiveArray(a), timestampToUsec(timestamp));
	}

	public static String csvPrimitiveArray(double[] a, Duration elapsedTime) {
		return String.format("%s,%d", csvPrimitiveArray(a), durationToUsec(elapsedTime));
	}
}
