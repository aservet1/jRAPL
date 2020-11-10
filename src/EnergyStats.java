package jrapl;

import java.time.Instant;
import java.time.Duration;

/** High-level representation of jrapl's energy stats. */
public final class EnergyStats extends EnergySample
{
	/** Returns the energy stats for each socket. */

	//Outsourcing this 'get' functionality entirely to the SyncEnergyMonitor class
	//public static EnergyStats[] get() {
	//	EnergyStats[] stats = new EnergyStats[ArchSpec.NUM_SOCKETS];
	//	double[] energy = EnergyCheckUtils.getEnergyStats();
	//	for (int i = 0; i < ArchSpec.NUM_SOCKETS; ++i) {
	//		int socket = i + 1;		
	//		stats[i] = new EnergyStats(socket, dram, core, cpu, pkg);
	//	}
	//	return stats;
	//}

	public EnergyStats(int socket) {
		super(socket, EnergyStringParser.toPrimitiveArray(EnergyMonitor.energyStatCheck(socket)));
	}
	
	public EnergyStats(int socket, double[] statsForSocket, Instant ts) {
		super(socket, statsForSocket, ts);
	}

	public EnergyStats(int socket, double[] statsForSocket) {
		super(socket, statsForSocket);
	}

	@Override
	public String dump() {
		return super.dump();
	}

	@Override
	public String toString() {
		return super.toString();
	}

}

