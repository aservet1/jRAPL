package jRAPL;

import java.time.Instant;
import java.time.Duration;

/** High-level representation of jrapl's energy stats. */
public final class EnergyStats extends EnergySample
{
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

