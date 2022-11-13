/**
 * 
 */
package org.powerapi.jjoules.rapl;

/**
 * @author sanoussy
 *
 */
public class RaplSubDomain extends RaplDomain {

	private final RaplDomain parent;

	/**
	 * @param socket
	 */
	protected RaplSubDomain(RaplDomain parent, String path) {
		super(path);
		this.parent = parent;
	}

	@Override
	public String toString() {
		return parent.toString() + "/" + getDomainName();
	}

	public static RaplDomain createSubDomain(RaplDomain raplDomain, String path) {
		RaplDomain domain = new RaplSubDomain(raplDomain, path);
		domain.checkDomainAvailable();
		return domain;
	}

	@Override
	public String getDomainKind() {
		return getDomainName();
	}
}
