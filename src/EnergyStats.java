package jrapl;


/** High-level representation of jrapl's energy stats. */
public final class EnergyStats extends JRAPL {
	/** Returns the energy stats for each socket. */
	public static EnergyStats[] get() {
		EnergyStats[] stats = new EnergyStats[NUM_SOCKETS];
		double[] energy = EnergyCheckUtils.getEnergyStats();
		for (int i = 0; i < NUM_SOCKETS; ++i) {
			stats[i] = new EnergyStats(i + 1, energy[3 * i + 1], energy[3 * i + 2], energy[3 * i]);
		}
		return stats;
	}

	private final int socket;
	private final double cpu;
	private final double pkg;
	private final double dram;

	public EnergyStats(int socket, double cpu, double pkg, double dram) {
		this.socket = socket;
		this.cpu = cpu;
		this.pkg = pkg;
		this.dram = dram;
	}

	public int getSocket() {
		return socket;
	}

	public double getCpu() {
		return cpu;
	}

	public double getPackage() {
		return pkg;
	}

	public double getDram() {
		return dram;
	}

	public EnergyStats difference(EnergyStats other) {
		double cpu = this.getCpu() - other.getCpu();
		if (cpu < 0) {
			cpu += ENERGY_WRAP_AROUND;
		}

		double pkg = this.getPackage() - other.getPackage();
		if (pkg < 0) {
			pkg += ENERGY_WRAP_AROUND;
		}

		double dram = this.getDram() - other.getDram();
		if (dram < 0) {
			dram += ENERGY_WRAP_AROUND;
		}

		return new EnergyStats(this.getSocket(), cpu, pkg, dram);
	}

	@Override
	public String toString() {
		return String.join(
			", ",
			"Socket: " + String.format("%d", socket),
			"CPU: " + String.format("%.4f", cpu),
			"Package: " + String.format("%.4f", pkg),
			"DRAM: " + String.format("%.4f", dram));
	}

	public static void main(String[] args) throws Exception {
		EnergyStats[] last = EnergyStats.get();
		while(true) {
			EnergyStats[] next = EnergyStats.get();
			for (int i = 0; i < NUM_SOCKETS; i++) {
				EnergyStats diff = next[i].difference(last[i]);
				System.out.println(diff);
				if (diff.getCpu() < 0 || diff.getPackage() < 0 || diff.getDram() < 0) {
					throw new RuntimeException("got a negative value!");
				}
			}
			last = next;
			Thread.sleep(40);
		}
	}
}
