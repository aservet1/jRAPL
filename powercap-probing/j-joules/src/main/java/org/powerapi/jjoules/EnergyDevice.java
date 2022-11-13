/**
 * 
 */
package org.powerapi.jjoules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class that abstract any device reporting energy consumption metrics
 *
 */
public abstract class EnergyDevice {
	private final Collection<EnergyDomain> availableDomains;
	private Collection<EnergyDomain> selectedDomains;

	/**
	 * @throws NoSuchEnergyDeviceException thrown if no domain was found for the
	 *                                     current device
	 * 
	 */
	public EnergyDevice() {
		this.availableDomains = this.listAvailableDomains();
		this.selectedDomains = this.availableDomains;
	}

	/**
	 * Selects specific energy domains to be monitored
	 * 
	 * @param domains all domains to configure
	 * @throws NoSuchDomainException
	 */
	public void selectDomain(Collection<EnergyDomain> domains) throws NoSuchDomainException {
		for (EnergyDomain domain : domains)
			if (!this.availableDomains.contains(domain))
				throw new NoSuchDomainException(domain);
		this.selectedDomains = domains;
	}

	/**
	 * Lists the specific domains to be monitored (by default all the available
	 * domains)
	 */
	public Collection<EnergyDomain> listSelectedDomains() {
		return this.selectedDomains;
	}

	public EnergySample recordEnergy() {
		return new EnergySample(this);
	}

	/**
	 * @return all available domain that could be monitored on the device
	 */
	public abstract Collection<EnergyDomain> listAvailableDomains();
	
	/**
	 * @return the energy consumed by device
	 * @throws DeviceNotConfiguredException
	 */
	protected abstract Map<EnergyDomain, Long> getDomainCounters();


	/**
	 * @return the energy consumed by device
	 * @throws DeviceNotConfiguredException
	 */
	protected abstract Map<EnergyDomain, Long> getMaxDomainCounters();

	public static final Map<String,Long> diff(final Map<String,Long> before, final Map<String,Long> after) {
		Set<String> keys = before.keySet();
		keys.retainAll(after.keySet());
		Map<String,Long> diff = new HashMap<String,Long>();
		for(String key:keys) {
			long value = Math.abs(after.get(key)-before.get(key));
			diff.put(key,value);
		}
		return diff;
	}
}
