package jRAPL;

import java.time.Instant;

/** High-level representation of jrapl's energy stats.
 * 	This class should only be used as an intermediate holder
 * 	for your sample data until you produce an EnergyMeasurement
 * 	between two of these. It's not really useful data on its own.
 */
public final class EnergySample {

	private Instant timestamp;
    private double[] raplCounters;
    
    public EnergySample(double[] raplCounters) {
		this.raplCounters = raplCounters.clone();
        this.timestamp = Instant.now();
    }

	public EnergySample(EnergySample other) {
		this.raplCounters = other.raplCounters.clone();
        this.timestamp = other.timestamp;
	}

	public String csv() {
		String s = new String();
		for (int i = 0; i < raplCounters.length; i++) {
			s += String.format("%.6f%s", raplCounters[i], EnergyMonitor.getCSVDelimiter());
		}
		return s + timestamp.toEpochMilli();
	}

    public String toString() {
        return csv();
    }

	public double[] getRaplCounters() {
		return raplCounters.clone();
	}

	public Instant getTimestamp() {
		return timestamp;
	}

}
