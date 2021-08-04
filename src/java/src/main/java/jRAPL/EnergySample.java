package jRAPL;

import java.util.Arrays;

// @TODO consider making EnergySamples's two subclasses be named EnergyStamp and EnergyLapse, instead of EnergyStats and EnergyDiff

public abstract class EnergySample
{
	private final double[] primitiveSample;
	protected native static String csvHeader();
	
	public EnergySample(double[] primitiveSample) {
		this.primitiveSample = Arrays.copyOfRange(
			primitiveSample, 0, ArchSpec.NUM_SOCKETS*ArchSpec.NUM_STATS_PER_SOCKET
		);
	}
	
	public EnergySample(EnergySample other) {
		this.primitiveSample = other.primitiveSample.clone();
	}

	protected String csv() {
		String s = new String();
		for (int i = 0; i < primitiveSample.length; i++) {
			s += String.format("%.6f,",primitiveSample[i]);
		}
		return s;
	}

	public double[] getPrimitiveSample() {
		return primitiveSample.clone();
	}
	private double getEnergy(int socket, int index) {
		if (index == -1) return -1; // index of -1 for a power domain means the power domain is not supported by your system
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
	
}
// 	protected static String dumpHeader() {
// 		return ArchSpec.ENERGY_STATS_STRING_FORMAT.replace("@",","); 
// 	}
// 
// 	public String dump() {
// 		String s = new String();
// 		for (int i = 0; i < primitiveSample.length; i++) {
// 			s += String.format("%.6f,",primitiveSample[i]);
// 		}
// 		return s;
// 	}

