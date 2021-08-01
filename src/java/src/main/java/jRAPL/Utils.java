/** Miscellaneous helper methods */

package jRAPL;

import java.time.Instant;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

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

}
