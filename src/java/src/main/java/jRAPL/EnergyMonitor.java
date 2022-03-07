package jRAPL;

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

	public static String getCSVDelimiter() {
		return csvDelimiter;
	}

	public EnergySample getEnergySample() {
        String raplString = NativeAccess.energyStatCheck();
        String[] raplStringParts = raplString.split("#");
        double[] raplData = new double[raplStringParts.length];
        for(int i = 0; i < raplData.length; ++i) {
            raplData[i] = Double.parseDouble(raplStringParts[i]);
        }
        return new EnergySample(raplData);
	}

}
