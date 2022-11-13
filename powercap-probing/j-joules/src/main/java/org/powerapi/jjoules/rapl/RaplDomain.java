/**
 * 
 */
package org.powerapi.jjoules.rapl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.powerapi.jjoules.EnergyDomain;
import org.powerapi.jjoules.NoSuchDomainException;

/**
 * Description of a RAPL domain
 *
 */
public class RaplDomain extends EnergyDomain {
	private static final String ENABLED = "enabled";
	private static final String NAME = "name";
	private static final String ENERGY_UJ = "energy_uj";
	private static final String MAX_ENERGY_RANGE_UJ = "max_energy_range_uj";

	private final File path;

	public static final RaplDomain createDomain(String domainPath) throws NoSuchDomainException {
		RaplDomain domain = new RaplDomain(domainPath);
		domain.checkDomainAvailable();
		return domain;
	}

	protected RaplDomain(String path) {
		this.path = new File(path);
	}

	/**
	 * @param pathName path to check existence
	 * @return true if pathName exist and false otherwise
	 */
	public boolean isDomainAvailable() {
		return this.path.exists();
	}

	public RaplDomain createSubDomain(String path) throws NoSuchDomainException {
		return RaplSubDomain.createSubDomain(this,path);
	}

	protected final void checkDomainAvailable() throws NoSuchDomainException {
		if (!isDomainAvailable())
			throw new NoSuchDomainException(this.path);
	}
	
	@Override
	public String getDomainName() throws NoSuchDomainException {
		checkDomainAvailable();
		return readDomainFile(this.path, NAME);
	}

	public boolean isDomainEnabled() throws NoSuchDomainException {
		checkDomainAvailable();
		return "1".equals(readDomainFile(this.path, ENABLED));
	}

	@Override
	public long getDomainCounter() throws NoSuchDomainException {
		checkDomainAvailable();
		return Long.parseLong(readDomainFile(this.path, ENERGY_UJ));
	}

	@Override
	public long getMaxDomainCounter() throws NoSuchDomainException {
		checkDomainAvailable();
		return Long.parseLong(readDomainFile(this.path, MAX_ENERGY_RANGE_UJ));
	}

	/**
	 * @param path
	 * @param pathName path to open and read content
	 * @return content of the file
	 */
	private String readDomainFile(File path, String file) throws NoSuchDomainException {
		try {
			FileReader fr = new FileReader(new File(path, file));
			String value = new BufferedReader(fr).readLine();
			fr.close();
			return value;
		} catch (Exception e) {
			throw new NoSuchDomainException(e);
		}
	}

	@Override
	public String toString() {
		return getDomainName();
	}

	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

	@Override
	public String getDomainKind() {
		return getDomainName().split("-")[0];
	}
}
