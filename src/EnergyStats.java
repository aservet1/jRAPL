package jrapl;

import java.time.Instant;
import java.time.Duration;


/** <h1> DOCUMENTATION OUT OF DATE </h1> High-level representation of jrapl's energy stats. */
public final class EnergyStats extends EnergySample
{
	/** <h1> DOCUMENTATION OUT OF DATE </h1> Returns the energy stats for each socket. */
	public static EnergyStats[] get() {
		EnergyStats[] stats = new EnergyStats[NUM_SOCKETS];
		double[] energy = EnergyCheckUtils.getEnergyStats();
		for (int i = 0; i < NUM_SOCKETS; ++i) {
			int socket = i + 1;
			double dram = energy[4 * i];
			double gpu = energy[4 * i + 1];
			double cpu = energy[4 * i + 2];
			double pkg = energy[4 * i + 3];
			stats[i] = new EnergyStats(/*socket,*/ dram, gpu, cpu, pkg);
		}
		return stats;
	}
	
	public EnergyStats(int socket, double dram, double gpu, double cpu, double pkg) {
		super(socket, dram, gpu, cpu, pkg);
	}


	public String commaSeparated() {
		return super.commaSeparated();
	}

	public EnergyDiff difference(EnergyStats other) {
		assert ( this.getSocket() == other.getSocket() );

		double cpu, pkg, dram, gpu;

		cpu = this.getCpu() - other.getCpu();
		if (cpu < 0) cpu += ENERGY_WRAP_AROUND;

		pkg = this.getPackage() - other.getPackage();
		if (pkg < 0) pkg += ENERGY_WRAP_AROUND;

		dram = -1;
		if (this.getDram() != -1) {
			dram = this.getDram() - other.getDram();
			if (dram < 0) dram += ENERGY_WRAP_AROUND;
		}

		gpu  = -1;
		if (this.getGpu() != -1) {
			gpu = this.getGpu() - other.getGpu();
			if (gpu < 0) gpu += ENERGY_WRAP_AROUND;
		}

		Duration elapsedTime = Duration.between(this.timestamp, other.timestamp);

		return new EnergyDiff(this.getSocket(), dram, gpu, cpu, pkg/*, elapsedTime*/);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public static void main(String[] args) throws Exception {
		EnergyStats[] last = EnergyStats.get();
		while(true) {
			EnergyStats[] next = EnergyStats.get();
			for (int i = 0; i < NUM_SOCKETS; i++) {
				EnergyDiff diff = next[i].difference(last[i]);
				System.out.println(last[i]);
				System.out.println(diff);
				System.out.println();
				double[] vals = {diff.getGpu(), diff.getCpu(), diff.getPackage(), diff.getDram()};
				for (double val : vals) {
					if ( val != -1 && val < 0) {
						System.out.println(diff);
						throw new RuntimeException("got a negative value!");
					}
				}
				if (diff.getGpu() < 0 || diff.getCpu() < 0 || diff.getPackage() < 0 || diff.getDram() < 0) {
					System.out.println(diff);
					throw new RuntimeException("got a negative value!");
				} 
			}
			last = next;
			Thread.sleep(40);
		}
	}
}

