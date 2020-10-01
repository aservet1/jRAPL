
package jrapl;

public final class EnergyDiff extends EnergySample
{
	private final Duration elapsedTime; //time between the two EnergyStamps

	public EnergyDiff(int socket, double dram, double gpu, double cpu, double pkg, Duration elapsed) {
		super(socket, dram, gpu, cpu, pkg);
		this.elapsedTime = elapsed;
	}

	public EnergyDiff(EnergyStats before, EnergyStats after)
	{
		assert ( before.getSocket() == after.getSocket() );

		cpu = before.getCpu() - after.getCpu();
		if (cpu < 0) cpu += ENERGY_WRAP_AROUND;

		pkg = before.getPackage() - after.getPackage();
		if (pkg < 0) pkg += ENERGY_WRAP_AROUND;

		//dram = -1;
		if (before.getDram() != -1) {
			dram = before.getDram() - after.getDram();
			if (dram < 0) dram += ENERGY_WRAP_AROUND;
		} else {
			dram = -1;
		}

		//gpu  = -1;
		if (before.getGpu() != -1) {
			gpu = before.getGpu() - after.getGpu();
			if (gpu < 0) gpu += ENERGY_WRAP_AROUND;
		} else {
			gpu = -1;
		}

		elapsedTime = Duration.between(before.timestamp, after.timestamp);

		return new EnergyDiff(before.getSocket(), dram, gpu, cpu, pkg, elapsedTime);


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
		while (true) {
			EnergyStats before = EnergyStats.get()[0];
			Thread.sleep(40);
			EnergyStats after = EnergyStats.get()[0];
			System.out.println(before.difference(after));
		}
	}
}





