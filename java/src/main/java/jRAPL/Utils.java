/** Miscellaneous helper methods */

package jRAPL;

class Utils {
    public static double[] stringToPrimitiveSample(String energyString)
	{
		String[] stringVals = energyString.split("@|,");
		double[] stats = new double[stringVals.length];
		for (int i = 0; i < stringVals.length; i++)
			stats[i] = Double.parseDouble(stringVals[i]);

		return stats;
	}

	public static EnergyStats stringToEnergyStats(String energyString)
	{
		return new EnergyStats(stringToPrimitiveSample(energyString));
    }
    
    public static double[] subtractPrimitiveSamples(double[] a, double[] b)
	{
		assert ( a.length == b.length );
		double[] diff = new double[a.length];
		for (int i = 0; i < diff.length; i++) {
			diff[i] = a[i] - b[i];
			if (diff[i] < 0)
				diff[i] += ArchSpec.RAPL_WRAPAROUND;
		}
		return diff;
    }
    
    public static String dumpPrimitiveArray(double[] a)
	{
		assert ( a.length == ArchSpec.NUM_SOCKETS * ArchSpec.NUM_STATS_PER_SOCKET );

		String s = new String();
		int index = 0;
		for (int socket = 1; socket < ArchSpec.NUM_SOCKETS; socket++) {
			for (int i = 0; i <= ArchSpec.NUM_STATS_PER_SOCKET-1; i++) {
				s += String.format("%4f",a[index++]) + ",";
			} s+= a[index] + "@";
		}
		return s;
		//String s = new String();
		//int i; for (i = 0; i < a.length-1; i++) {
		//	s += String.format("%4f",a[i]) + ",";
		//} s += String.format("%4f",a[i]);
		//return s;
	}
}
