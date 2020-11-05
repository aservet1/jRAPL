package jrapl;

import java.time.Instant;
import java.time.Duration;

/** High-level representation of jrapl's energy stats. */
public final class EnergyStats extends EnergySample
{
	/** Returns the energy stats for each socket. */
	public static EnergyStats[] get() {
		EnergyStats[] stats = new EnergyStats[ArchSpec.NUM_SOCKETS];
		double[] energy = EnergyCheckUtils.getEnergyStats();
		for (int i = 0; i < ArchSpec.NUM_SOCKETS; ++i) {
			int socket = i + 1;
			double dram = energy[4 * i];
			double gpu = energy[4 * i + 1];
			double cpu = energy[4 * i + 2];
			double pkg = energy[4 * i + 3];
			stats[i] = new EnergyStats(socket, dram, gpu, cpu, pkg);
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
		
		int _socket = this.getSocket();
		double _dram, _gpu, _cpu, _pkg;
		Duration _elapsedTime;

		_cpu = this.getCpu() - other.getCpu();
		if (_cpu < 0) _cpu += ArchSpec.ENERGY_WRAP_AROUND;

		_pkg = this.getPackage() - other.getPackage();
		if (_pkg < 0) _pkg += ArchSpec.ENERGY_WRAP_AROUND;

		if (this.getDram() != -1) {
			_dram = this.getDram() - other.getDram();
			if (_dram < 0) _dram += ArchSpec.ENERGY_WRAP_AROUND;
		} else {
			_dram = -1;
		}

		if (this.getGpu() != -1) {
			_gpu = this.getGpu() - other.getGpu();
			if (_gpu < 0) _gpu += ArchSpec.ENERGY_WRAP_AROUND;
		} else {
			_gpu = -1;
		}

		_elapsedTime = Duration.between(this.timestamp, other.timestamp);

		return new EnergyDiff(_socket, _dram, _gpu, _cpu, _pkg, _elapsedTime);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public static void main(String[] args) throws Exception {

		EnergyManager manager = new EnergyManager();
		manager.init();

		EnergyStats[] last = EnergyStats.get();
		while(true) {
			EnergyStats[] next = EnergyStats.get();
			for (int i = 0; i < ArchSpec.NUM_SOCKETS; i++) {
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
		} // infinite loop
		
		//manager.cleanup(); -- unreachable
	}
}

