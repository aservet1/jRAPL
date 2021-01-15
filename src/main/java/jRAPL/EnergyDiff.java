
package jRAPL;

import java.time.Instant;
import java.time.Duration;

public final class EnergyDiff extends EnergySample
{
	private Duration elapsedTime = null; //time between the two EnergyStamps

	public EnergyDiff(int socket, double[] statsForSocket, Duration elapsedTime) {
		super(socket, statsForSocket);
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
		assert after.socket == before.socket;
		assert after.stats.length == before.stats.length;		

		double[] statsDiff = new double[before.stats.length];
		for (int i = 0; i < after.stats.length; i++) {
			statsDiff[i] = after.stats[i] - before.stats[i];
			if (statsDiff[i] < 0) statsDiff[i] += ArchSpec.RAPL_WRAPAROUND;
		}

		return new EnergyDiff (	before.socket,
								statsDiff,
								(before.timestamp == null || after.timestamp == null)
								? null
								: Duration.between(before.timestamp, after.timestamp)	);
	}

	@Override
	public String toString() {
		return String.join(
			", ",
			super.toString(),
			"Duration (nanoseconds): " + this.elapsedTime.toNanos()
		);	
	}

}





