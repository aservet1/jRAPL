package jRAPL;

import java.util.Arrays;

import java.time.Instant;

// @TODO consider making EnergySamples's two subclasses be named EnergyStamp and EnergyLapse, instead of EnergyStats and EnergyDiff

public abstract class EnergySample
{
	public static class PerSocketSample {

		private final int socketNumber;
		private final double[] primitiveSample;
		
		public PerSocketSample(int socketNumber, double[] primitiveSample) {
			this.socketNumber = socketNumber;
			this.primitiveSample = primitiveSample;
		}

		private PerSocketSample(PerSocketSample other) {
			this.socketNumber = other.socketNumber;
			this.primitiveSample = other.primitiveSample.clone();
		}

		public int getSocketNumber() {
			return socketNumber;
		}

		public double getCore() {
			return (ArchSpec.CORE_IDX == -1) ? -1.0 : this.primitiveSample[ArchSpec.CORE_IDX];
		}
	
		public double getGpu() {
			return (ArchSpec.GPU_IDX == -1) ? -1.0 : this.primitiveSample[ArchSpec.GPU_IDX];
		}
	
		public double getPackage() {
			return (ArchSpec.PKG_IDX == -1) ? -1.0 : this.primitiveSample[ArchSpec.PKG_IDX];
		}
	
		public double getDram() {
			return (ArchSpec.DRAM_IDX == -1) ? -1.0 : this.primitiveSample[ArchSpec.DRAM_IDX];
		}
		
		public String dump() {
			
			String joinedStats = new String();
			int i = 0;
			for (; i < primitiveSample.length-1; i++) joinedStats += String.format("%.4f", primitiveSample[i]) + ",";
			joinedStats += String.format("%.4f",primitiveSample[i]);
	
			return String.join(
				",",
				String.format("%d", socketNumber),joinedStats);
	
		}

		@Override
		public String toString() {
			switch (ArchSpec.ENERGY_STATS_STRING_FORMAT.split("@")[0]) {
				case "dram,gpu,core,pkg":
					return String.format("DRAM: %.4f, GPU: %.4f, Package: %.4f, Core: %.4f, ",
											primitiveSample[ArchSpec.DRAM_IDX],
											primitiveSample[ArchSpec.GPU_IDX],
											primitiveSample[ArchSpec.PKG_IDX],
											primitiveSample[ArchSpec.CORE_IDX]
										);
				case "gpu,core,pkg":
					return String.format("GPU: %.4f, Package: %.4f, Core: %.4f, ",
											primitiveSample[ArchSpec.GPU_IDX],
											primitiveSample[ArchSpec.PKG_IDX],
											primitiveSample[ArchSpec.CORE_IDX]
										);
				case "dram,core,pkg":
					return String.format("DRAM: %.4f, Package: %.4f, Core: %.4f, ",
											primitiveSample[ArchSpec.DRAM_IDX],
											primitiveSample[ArchSpec.PKG_IDX],
											primitiveSample[ArchSpec.CORE_IDX]
										);
				default:
					System.err.println("PerSocketSample::toString(): ENERGY_STATS_STRING_FORMAT not supported !!: "
										+ ArchSpec.ENERGY_STATS_STRING_FORMAT);
					System.exit(1);
					return null;
			}
		}

		public double[] getPrimitiveSample() {
			return primitiveSample.clone();
		}

	}

	private PerSocketSample[] perSocketSamples;
	protected Instant timestamp;

	public EnergySample(double[] primitiveSample, Instant timestamp)
	{
		this.perSocketSamples = new PerSocketSample[ArchSpec.NUM_SOCKETS];
		
		int lo = 0, hi = ArchSpec.NUM_STATS_PER_SOCKET;
		for (int i = 0; i < ArchSpec.NUM_SOCKETS; i++) {
			int socketNumber = i+1;
			perSocketSamples[i] = new PerSocketSample(socketNumber, Arrays.copyOfRange(primitiveSample, lo, hi));
		}
		
		this.timestamp = timestamp;
	}

	public EnergySample(double[] primitiveSample)
	{
		this.perSocketSamples = new PerSocketSample[ArchSpec.NUM_SOCKETS];
		int lo = 0, hi = ArchSpec.NUM_STATS_PER_SOCKET;
		for (int i = 0; i < ArchSpec.NUM_SOCKETS; i++) {
			int socketNumber = i+1;
			perSocketSamples[i] = new PerSocketSample(socketNumber, Arrays.copyOfRange(primitiveSample, lo, hi));
			lo = hi;
			hi += ArchSpec.NUM_STATS_PER_SOCKET;
		}
		timestamp = Instant.now();
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant ts)
	{
		assert this.timestamp == null;
		this.timestamp = ts;
	}
	
	public PerSocketSample atSocket(int socket) {
		return new PerSocketSample(perSocketSamples[socket-1]);
	}

	public double[] getPrimitiveSample() {
		double[] primitiveSample = new double[ArchSpec.NUM_SOCKETS*ArchSpec.NUM_STATS_PER_SOCKET];
		int index = 0;
		for (int socket = 1; socket <= ArchSpec.NUM_SOCKETS; socket++) {
			double[] currentPrimitive = this.atSocket(socket).getPrimitiveSample();
			for (int i = 0; i < currentPrimitive.length; i++) primitiveSample[index++] = currentPrimitive[i];
		}
		return primitiveSample;
	}

	public String dump() {
		String s = new String();
		int n = ArchSpec.NUM_SOCKETS;
		for (int socket = 1; socket <= n-1; socket++) {
			s += this.atSocket(socket).dump()+",";
		} s += this.atSocket(n).dump();
		return s;
	}
}
