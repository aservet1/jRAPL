package jRAPL;

import java.util.Arrays;

// @TODO consider making EnergySamples's two subclasses be named EnergyStamp and EnergyLapse, instead of EnergyStats and EnergyDiff

public abstract class EnergySample
{
	private final double[] primitiveSample;
	
	public EnergySample(double[] primitiveSample) {
		this.primitiveSample = primitiveSample;
	}
	
	public EnergySample(EnergySample other) {
		this.primitiveSample = other.primitiveSample.clone();
	}

	private double getEnergy(int socket, int index) {
		int socketOffset = (socket-1)*ArchSpec.NUM_STATS_PER_SOCKET;
		return primitiveSample[socketOffset + index];
	}
	public double getCore(int socket) {
		return getEnergy(socket, ArchSpec.CORE_IDX);
	}
	public double getCore() {
		double result = 0;
		for (int s = 1; s <= ArchSpec.NUM_SOCKETS; s++) {
			result += getCore(s);
		} return result;
	}
	public double getGpu(int socket) {
		return getEnergy(socket, ArchSpec.GPU_IDX);
	}
	public double getGpu() {
		double result = 0;
		for (int s = 1; s <= ArchSpec.NUM_SOCKETS; s++) {
			result += getGpu(s);
		} return result;
	}
	public double getPackage(int socket) {
		return getEnergy(socket, ArchSpec.PKG_IDX);
	}
	public double getPackage() {
		double result = 0;
		for (int s = 1; s <= ArchSpec.NUM_SOCKETS; s++) {
			result += getPackage(s);
		} return result;
	}
	public double getDram(int socket) {
		return getEnergy(socket, ArchSpec.DRAM_IDX);
	}
	public double getDram() {
		double result = 0;
		for (int s = 1; s <= ArchSpec.NUM_SOCKETS; s++) {
			result += getDram(s);
		} return result;
	}
	
	protected static String dumpHeader() {
		return ArchSpec.ENERGY_STATS_STRING_FORMAT.replace("@",","); 
	}

	public String dump() {
		String s = new String();
		for (int i = 0; i < primitiveSample.length; i++) {
			s += String.format("%.4f,",primitiveSample[i]);
			// if (i != primitiveSample.length-1) s += ",";
		}
		return s;
	}

	public double[] getPrimitiveSample() {
		return primitiveSample.clone();
	}

	
	// public String dump() {
	// 	String s = new String();
	// 	int n = ArchSpec.NUM_SOCKETS;
	// 	for (int socket = 1; socket <= n-1; socket++) {
	// 		s += this.atSocket(socket).dump()+",";
	// 	} s += this.atSocket(n).dump();
	// 	return s;
	// }
	
	// @Override
	// public String toString() {
	// 	String s = new String();
	// 	for (int i = 0; i < primitiveSample.length; i++) {
	// 		s += String.format("%.4f,",primitiveSample[i]);
	// 	} if (timestamp) s += Long.toString(ChronoUnit.MICROS.between(Instant.EPOCH, timestamp));
	// 	return s;
	// }
		// switch (ArchSpec.ENERGY_STATS_STRING_FORMAT.split("@")[0]) {
		// 	case "dram,gpu,core,pkg":
		// 		return String.format("DRAM: %.4f, GPU: %.4f, Package: %.4f, Core: %.4f, ",
		// 								primitiveSample[ArchSpec.DRAM_IDX],
		// 								primitiveSample[ArchSpec.GPU_IDX],
		// 								primitiveSample[ArchSpec.PKG_IDX],
		// 								primitiveSample[ArchSpec.CORE_IDX]
		// 							);
		// 	case "gpu,core,pkg":
		// 		return String.format("GPU: %.4f, Package: %.4f, Core: %.4f, ",
		// 								primitiveSample[ArchSpec.GPU_IDX],
		// 								primitiveSample[ArchSpec.PKG_IDX],
		// 								primitiveSample[ArchSpec.CORE_IDX]
		// 							);
		// 	case "dram,core,pkg":
		// 		return String.format("DRAM: %.4f, Package: %.4f, Core: %.4f, ",
		// 								primitiveSample[ArchSpec.DRAM_IDX],
		// 								primitiveSample[ArchSpec.PKG_IDX],
		// 								primitiveSample[ArchSpec.CORE_IDX]
		// 							);
		// 	default:
		// 		System.err.println("PerSocketSample::toString(): ENERGY_STATS_STRING_FORMAT not supported !!: "
		// 							+ ArchSpec.ENERGY_STATS_STRING_FORMAT);
		// 		System.exit(1);
		// 		return null;
		// }

	// public double[] getPrimitiveSample() {
	// 	double[] primitiveSample = new double[ArchSpec.NUM_SOCKETS*ArchSpec.NUM_STATS_PER_SOCKET];
	// 	int index = 0;
	// 	for (int socket = 1; socket <= ArchSpec.NUM_SOCKETS; socket++) {
	// 		double[] currentPrimitive = this.atSocket(socket).getPrimitiveSample();
	// 		for (int i = 0; i < currentPrimitive.length; i++) primitiveSample[index++] = currentPrimitive[i];
	// 	}
	// 	return primitiveSample;
	// }

}
