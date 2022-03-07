package jRAPL;

import java.time.Instant;

/** High-level representation of jrapl's energy stats. */
public final class EnergySample {

	private Instant timestamp;
    private double[] raplCounters;
    
    public EnergySample(double[] raplData) {
		this.raplCounters = raplCounters.clone();
        this.timestamp = Instant.now();
    }

	public EnergySample(EnergySample other) {
		this.raplData = other.raplCounters.clone();
        this.timestamp = other.timestamp;
	}

	public String csv() {
		String s = new String();
		for (int i = 0; i < raplCounters.length; i++) {
			s += String.format("%.6f%s", primitiveSample[i], EnergyMonitor.getCSVDelimiter());
		}
		return s + timestamp;
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
