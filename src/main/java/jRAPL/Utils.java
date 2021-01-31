/** Miscellaneous helper methods */

package jRAPL;

class Utils {
    public static double[] stringToPrimitiveArray(String energyString)
	{
		String[] stringVals = energyString.split("@|,");
		double[] stats = new double[stringVals.length];
		for (int i = 0; i < stringVals.length; i++)
			stats[i] = Double.parseDouble(stringVals[i]);

		return stats;
	}

	public static EnergyStats[] stringToObjectArray(String energyString)
	{
		String[] perSocketEnergyString = energyString.split("@");
		EnergyStats[] stats = new EnergyStats[perSocketEnergyString.length];
		for (int i = 0; i < perSocketEnergyString.length; i++) {
			String[] statsStrings = perSocketEnergyString[i].split(","); 
			double[] statsNums = new double[statsStrings.length];
			for (int j = 0; j < statsNums.length; j++)
				statsNums[j] = Double.parseDouble(statsStrings[j]);
			int socket = i+1;
			stats[i] = new EnergyStats(socket, statsNums, null);
		}
		return stats;
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
		String s = new String();
		int i; for (i = 0; i < a.length-1; i++) {
			s += String.format("%4f",a[i]) + ",";
		} s += String.format("%4f",a[i]);
		return s;
	}
}