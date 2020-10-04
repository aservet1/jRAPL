
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

	/*public EnergyDiff(EnergyStats before, EnergyStats after)
	{
		assert ( before.getSocket() == after.getSocket() );
		
		this.socket = before.getSocket();

		this.cpu = before.getCpu() - after.getCpu();
		if (this.cpu < 0) this.cpu += ENERGY_WRAP_AROUND;

		this.pkg = before.getPackage() - after.getPackage();
		if (this.pkg < 0) this.pkg += ENERGY_WRAP_AROUND;

		//dram = -1;
		if (before.getDram() != -1) {
			this.dram = before.getDram() - after.getDram();
			if (this.dram < 0) this.dram += ENERGY_WRAP_AROUND;
		} else {
			this.dram = -1;
		}

		//gpu  = -1;
		if (before.getGpu() != -1) {
			this.gpu = before.getGpu() - after.getGpu();
			if (this.gpu < 0) gpu += ENERGY_WRAP_AROUND;
		} else {
			this.gpu = -1;
		}

		this.elapsedTime = Duration.between(before.timestamp, after.timestamp);


	}*/

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
		while (true) {
			EnergyStats before = EnergyStats.get()[0];
			Thread.sleep(40);
			EnergyStats after = EnergyStats.get()[0];
			System.out.println(after.difference(before));
		}
	}
}





