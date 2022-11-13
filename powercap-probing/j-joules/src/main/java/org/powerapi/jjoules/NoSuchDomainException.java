/**
 * 
 */
package org.powerapi.jjoules;

import java.io.File;

/**
 * Domain-level exception
 *
 */
public class NoSuchDomainException extends RuntimeException {
	private static final long serialVersionUID = 5374376192288288350L;

	public NoSuchDomainException(File domain) {
		super("No domain available for path " + domain.getAbsolutePath());
	}

	public NoSuchDomainException(EnergyDomain domain) {
		super("No domain available for " + domain);
	}

	public NoSuchDomainException(Exception e) {
		super(e);
	}
}
