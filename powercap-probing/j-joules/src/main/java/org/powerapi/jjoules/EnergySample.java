/**
 * 
 */
package org.powerapi.jjoules;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * class for sampling the energy of a code snippet
 * 
 */
public class EnergySample {
	public static final String ENERGY_UNIT = "uJ"; // Micro-Joules
	public static final String POWER_UNIT = "mW"; // Milli-Watts
	public static final String TIME_UNIT = "ns"; // Nano-Seconds
	public static final String DEVICE = "device";
	public static final String DURATION = "duration|"+TIME_UNIT;

	private final EnergyDevice device;
	private final Map<EnergyDomain, Long> maxCounters;
	private final Map<EnergyDomain, Long> initialCounters;
	private final long initialTimestamp = timestamp();

	public EnergySample(EnergyDevice device) {
		this.device = device;
		this.maxCounters = device.getMaxDomainCounters();
		this.initialCounters = device.getDomainCounters();
	}

	/**
	 * @return the current timestamp (in milliseconds)
	 */
	private final static long timestamp() {
		return System.nanoTime();
	}

	/**
	 * Builds the energy report from counters.
	 * 
	 * @param currentCounters  current value for all selected counters
	 * @param currentTimestamp current timestamp.
	 * @return the generated report.
	 */
	private final Map<String, Long> buildReport(Map<EnergyDomain, Long> currentCounters, long currentTimestamp) {
		Map<String, Long> report = new HashMap<String, Long>();

		long duration = currentTimestamp - initialTimestamp;
		report.put(DURATION, duration);

		long device = 0;
		for (Entry<EnergyDomain, Long> initial : initialCounters.entrySet()) {
			EnergyDomain domain = initial.getKey();
			long value = currentCounters.get(domain);
			if (value >= initial.getValue())
				value = value - initial.getValue();
			else // Counter reached its max value before reset
				value = this.maxCounters.get(domain) - initial.getValue() + value;
			String name = domain.toString();
			report.put(energy(name), value);
			report.put(power(name), convertToPower(duration,value));

			// Computes aggregated values per domain
			String kind = domain.getDomainKind();
			long aggregate = value;
			if (report.containsKey(energy(kind))) {
				aggregate += report.get(energy(kind));
			}
			report.put(energy(kind), aggregate);
			report.put(power(kind), convertToPower(duration,aggregate));

			// Computes the overall consumption of the device
			if (kind.equals("package")||kind.equals("dram"))
				device += value;
		}

		if (device > 0) {
			report.put(energy(DEVICE), device);
			report.put(power(DEVICE), convertToPower(duration,device));
		}

		return report;
	}

	private static final String energy(final String text) {
		return text + "|" + ENERGY_UNIT;
	}

	private static final String power(final String text) {
		return text + "|" + POWER_UNIT;
	}

	private static final long convertToPower(final long duration, final long energy) {
		return energy * 1000000 / duration;
	}


	private boolean stopped = false;

	private Map<String, Long> report;

	/**
	 * Stops the sampling processing and returns the final energy report.
	 * 
	 * @return the sampled energy
	 */
	public Map<String, Long> stop() {
		if (stopped)
			return report;
		this.report = buildReport(device.getDomainCounters(), timestamp());
		return report;
	}

	/**
	 * Reports on the energy consumed so far (but does not stop the sampling).
	 * 
	 * @return the energyConsumed
	 */
	public Map<String, Long> getEnergyReport() {
		return stopped ? report : buildReport(device.getDomainCounters(), timestamp());
	}
}
