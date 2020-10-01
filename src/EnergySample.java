package jrapl;

import java.time.Instant;

public abstract class EnergySample extends JRAPL
{
	protected final int socket;
	protected final double dram;
	protected final double gpu;
	protected final double cpu;
	protected final double pkg;
	protected final Instant timestamp;

	public EnergySample(int socket, double dram, double gpu, double cpu, double pkg){
		this.socket = socket;	
		this.dram = dram;
		this.gpu = gpu;
		this.cpu = cpu;
		this.pkg = pkg;
		this.timestamp = Instant.now();
	}

	public int getSocket() {
		return socket;
	}

	public double getCpu() {
		return cpu;
	}

	public double getGpu() {
		return gpu;
	}

	public double getPackage() {
		return pkg;
	}

	public double getDram() {
		return dram;
	}
	
	public String commaSeparated() {
		return String.join(
			",",
			String.format("%d", socket),
			String.format("%.4f", dram),
			String.format("%.4f", gpu),
			String.format("%.4f", cpu),
			String.format("%.4f",pkg),
			timestamp.toString()
		);
	}
	
	@Override
	public String toString() {
		return String.join(
			", ",
			"Socket: " + String.format("%d", socket),
			"CPU: " + String.format("%.4f", cpu),
			"Package: " + String.format("%.4f", pkg),
			"DRAM: " + String.format("%.4f", dram),
			"GPU: " + String.format("%.4f",gpu)//,
			"Timestamp: " + timestamp.toString()
			);
	}

}
