package org.powerapi.jjoules.rapl;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Basic tests for running J-Joules
 *
 */
class RaplDeviceTest {
	private RaplDevice device;

	@BeforeEach
	public void initDevice() {
		this.device = RaplDevice.RAPL;
		Assumptions.assumeTrue(this.device.isDeviceAvailable(), "Intel RAPL is not available on this computer");
		Assumptions.assumeTrue(this.device.isDeviceEnabled(), "Intel RAPL is not enabled on this computer");
	}

	@Test
	public void listOfAvailableDomainsShouldBeAboveZero() {
		Assertions.assertTrue(this.device.listAvailableDomains().size() > 0);
	}

	@Test
	public void reportEnergyShoudBeAboveOrEqualsToZero() {
		Map<String, Long> report = this.device.recordEnergy().stop();
		for (long value : report.values())
			Assertions.assertTrue(value >= 0);
	}

	@Test
	public void diffWithEmptyshouldBeEmpty() {
		Map<String, Long> report = this.device.recordEnergy().stop();
		Map<String, Long> empty = new HashMap<String,Long>();
		Map<String, Long> diff = this.device.diff(report,empty);
		Assertions.assertTrue(diff.size() == 0);
	}

	@Test
	public void diffWithItSelfShouldReturnZeros() {
		Map<String, Long> report = this.device.recordEnergy().stop();
		Map<String, Long> diff = this.device.diff(report,report);
		Assertions.assertTrue(diff.size() == report.size());
		for (long value : diff.values()){
			Assertions.assertTrue(value == 0);
		}
	}
}
