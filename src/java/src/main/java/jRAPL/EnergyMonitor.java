package jRAPL;

import java.util.Arrays;
import java.time.Instant;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class EnergyMonitor {

	private boolean active = false;

	public void activate() {
		if (active) {
			System.err.println(
				"Error: "
				+ getClass().getName()
				+ "@"
				+ Integer.toHexString(hashCode())
				+ " already activated."
				+ " Double activate is not allowed. Exiting program."
			);
			System.exit(1);
		}
		active = true;
		NativeAccess.subscribe();
	}

	public void deactivate() {
		if (!active) {
			System.err.println(
				"Error: "
				+ getClass().getName()
				+ "@"
				+ Integer.toHexString(hashCode())
				+ " already deactivated."
				+ " Double deactivate is not allowed. Exiting program."
			);
			System.exit(1);
		}
		active = false;
        NativeAccess.unsubscribe();
	}

	private static String csvDelimiter = ",";

	private static double stringToDoubleConsideringComma(String s) {
		double d = 0;
		try {
			d = Double.parseDouble(s.replace(",","."));
		} catch (NumberFormatException ex) {
			System.err.println("ERROR turning string to a double: " + s);
			ex.printStackTrace();
			System.exit(1);
		}
		return d;
	}

	public EnergySample getSample() {
        String raplString = NativeAccess.energyStatCheck();
        String[] raplStringParts = raplString.split("#");
        double[] raplData = new double[raplStringParts.length];
        for(int i = 0; i < raplData.length; ++i) {
            raplData[i] = Double.toString(raplStringParts[i]);
        }
        return new EnergySample(raplData);
	}

}
