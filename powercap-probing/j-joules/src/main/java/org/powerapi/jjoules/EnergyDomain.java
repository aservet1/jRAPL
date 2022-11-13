/**
 * 
 */
package org.powerapi.jjoules;

/**
 * Class that provides an abstraction of any domain exposed by a device.
 * 
 */
public abstract class EnergyDomain {
	/**
	 * @return energy consumed by domain
	 */
	public abstract long getDomainCounter();

	/**
	 * @return energy consumed by domain
	 */
	public abstract long getMaxDomainCounter();

	/**
	 * @return domain name
	 */
	public abstract String getDomainName();

	public abstract String getDomainKind();

	public abstract boolean isDomainAvailable();
}
