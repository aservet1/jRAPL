
package jrapl;

public final class EnergyDiff extends EnergySample
{
	private final Duration elapsedTime; //time between the two EnergyStamps

	public EnergyDiff(int socket, double dram, double gpu, double cpu, double pkg, Duration elapsed) {
		super(socket, dram, gpu, cpu, pkg);
		this.elapsedTime = elapsed;
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
	
	}
}
