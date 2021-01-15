package jRAPL;

import java.time.Instant;
import java.time.Duration;

public abstract class EnergySample
{

	protected final int socket;
	
	protected final double[] stats;
	protected Instant timestamp;

	public EnergySample(int socket, double[] statsForSocket, Instant timestamp)
	{
		this.socket = socket;
		this.stats = statsForSocket;
		this.timestamp = timestamp;
	}

	public EnergySample(int socket, double[] statsForSocket)
	{
		this.socket = socket;
		this.stats = statsForSocket;
		this.timestamp = Instant.now();
	}

	public int getSocket() {
		return this.socket;
	}

	public double getCore() {
		return this.stats[ArchSpec.CORE_ARRAY_INDEX];
	}

	public double getGpu() {
		return this.stats[ArchSpec.GPU_ARRAY_INDEX];
	}

	public double getPackage() {
		return this.stats[ArchSpec.PKG_ARRAY_INDEX];
	}

	public double getDram() {
		return this.stats[ArchSpec.DRAM_ARRAY_INDEX];
	}
	
	public String dump() {
		
		String joinedStats = new String();
		int i = 0;
		for (; i < stats.length-1; i++) joinedStats += String.format("%4f", stats[i]) + ",";
		joinedStats += String.format("%4f",stats[i]);

		return String.join(
			",",
			String.format("%d", socket),
			joinedStats,
			(timestamp == null)
				? "null"
				: Long.toString(
					Duration.between(
							Instant.EPOCH,
							timestamp
						).toNanos()/1000 //microseconds
					)
		);

	}

	public void setTimestamp(Instant ts)
	{
		assert this.timestamp == null;
		this.timestamp = ts;
	}


	@Override
	public String toString() {
		//System.out.println(Arrays.toString(stats));
		String labeledStats = new String();
		if (ArchSpec.DRAM_ARRAY_INDEX != -1) labeledStats += "DRAM: " + String.format("%.4f", stats[ArchSpec.DRAM_ARRAY_INDEX]) + ", ";
		if (ArchSpec.GPU_ARRAY_INDEX != -1)  labeledStats += "GPU: " + String.format("%.4f", stats[ArchSpec.GPU_ARRAY_INDEX]) + ", ";
		if (ArchSpec.PKG_ARRAY_INDEX != -1)  labeledStats += "Package: " + String.format("%.4f", stats[ArchSpec.PKG_ARRAY_INDEX]) + ", ";
		if (ArchSpec.CORE_ARRAY_INDEX != -1) labeledStats += "Core: " + String.format("%.4f", stats[ArchSpec.CORE_ARRAY_INDEX]) + ", ";

		if (labeledStats.length() == 0) labeledStats = "No power domains supported, ";
		String timestampString = (timestamp == null) ? ("null")
								: ("Timestamp (usecs since epoch): " 
									+ Duration.between(Instant.EPOCH, timestamp)
									.toNanos()/1000);

		return String.format("Socket: %d, ", socket) + labeledStats + timestampString; 
	}

}
