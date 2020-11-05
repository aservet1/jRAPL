
package jrapl;

import java.time.Instant;
import java.time.Duration;

public final class EnergyDiff extends EnergySample
{
	private final Duration elapsedTime; //time between the two EnergyStamps

	public EnergyDiff(int socket, double dram, double gpu, double cpu, double pkg, Duration elapsedTime) {
		super(socket, dram, gpu, cpu, pkg);
		this.elapsedTime = elapsedTime;
	}

	public Duration getElapsedTime() {
		return this.elapsedTime;
	}

	public String commaSeparated() {
		return String.join(
			",",
			super.commaSeparated(),
			this.elapsedTime.toString()
		);
	}

	@Override
	public String toString() {
		return String.join(
			", ",
			super.toString(),
			"Duration: " + this.elapsedTime.toString()
		);	
	}

	public static void main(String[] args) throws Exception {
		JRAPL.profileInit();
	
		while (true) {
			EnergyStats before = EnergyStats.get()[0];
			Thread.sleep(40);
			EnergyStats after = EnergyStats.get()[0];
			System.out.println(after.difference(before));
		}

		//JRAPL.profileDealloc(); -- unreachable
	}
}





