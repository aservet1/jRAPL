package jrapl;

public class EnergyStringParser {

	public static double[] toPrimitiveArray(String energyString)
	{
		String[] stringVals = energyString.split("@|,");
		double[] stats = new double[stringVals.length];
		for (int i = 0; i < stringVals.length; i++)
			stats[i] = Double.parseDouble(stringVals[i]);

		return stats;
	}

	public static EnergyStats[] toObjectArray(String energyString)
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

}
