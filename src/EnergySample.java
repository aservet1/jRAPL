package jrapl;

import java.time.Instant;
import java.time.Duration;

//import java.util.Arrays;

// TODO make sure you make the change from cpu to core across the board

public abstract class EnergySample
{
	private static final int DRAM_INDEX;
	private static final int GPU_INDEX;
	private static final int CORE_INDEX;
	private static final int PKG_INDEX;
	//TODO -- there's a 5th possible power domain, right? like full motherboard energy or something

	static {
		int dramIndex = -1, gpuIndex = -1, coreIndex = -1, pkgIndex = -1;
		String domains_string = ArchSpec.ENERGY_STATS_STRING_FORMAT.split("@")[0];
		String[] positions = domains_string.split(",");
		for ( int i = 0; i < positions.length; i++ ) {
			switch (positions[i]) {
				case "dram":
					dramIndex = i;
					break;
				case "gpu":
					gpuIndex = i;
					break;
				case "core":
					coreIndex = i;
					break;
				case "pkg":
					pkgIndex = i;
					break;
				default:
					System.err.println("unexpected format string: " + domains_string);
					System.exit(0);
			}	
		}
		DRAM_INDEX = dramIndex;
		GPU_INDEX = gpuIndex;
		CORE_INDEX = coreIndex;
		PKG_INDEX = pkgIndex;
		//System.out.println(DRAM_INDEX+" "+GPU_INDEX +" "+CORE_INDEX+" "+PKG_INDEX);

	}
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
		return this.stats[CORE_INDEX];
	}

	public double getGpu() {
		return this.stats[GPU_INDEX];
	}

	public double getPackage() {
		return this.stats[PKG_INDEX];
	}

	public double getDram() {
		return this.stats[DRAM_INDEX];
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
		if (DRAM_INDEX != -1) labeledStats += "DRAM: " + String.format("%.4f", stats[DRAM_INDEX]) + ", ";
		if (GPU_INDEX != -1)  labeledStats += "GPU: " + String.format("%.4f", stats[GPU_INDEX]) + ", ";
		if (PKG_INDEX != -1)  labeledStats += "Package: " + String.format("%.4f", stats[PKG_INDEX]) + ", ";
		if (CORE_INDEX != -1) labeledStats += "Core: " + String.format("%.4f", stats[CORE_INDEX]) + ", ";

		if (labeledStats.length() == 0) labeledStats = "No power domains supported, ";
		String timestampString = (timestamp == null) ? ("null")
								: ("Timestamp (usecs since epoch): " 
									+ Duration.between(Instant.EPOCH, timestamp)
									.toNanos()/1000);

		return String.format("Socket: %d, ", socket) + labeledStats + timestampString; 
	}

}
