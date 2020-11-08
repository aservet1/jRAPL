package jrapl;

public class EnergyStringParser {

	public static double[] toPrimitiveArray(String energyString)
	{
		String[] perSockEner = energyString.split("@");
		double[] stats = new double[ArchSpec.NUM_SOCKETS*ArchSpec.NUM_STATS_PER_SOCKET]; // 4 stats per socket

		for(int i = 0; i < perSockEner.length * ArchSpec.NUM_STATS_PER_SOCKET; ) {
			String[] energy = perSockEner[i / ArchSpec.NUM_STATS_PER_SOCKET].split(",");
			for(int j = 0; j < energy.length; j++, i++) {
				stats[i] = Double.parseDouble(energy[j]);
			}
		}
		return stats;
	}

	public static EnergyStats[] toObjectArray(String energyString)
	{
		String[] perSockEner = energyString.split("@");
		EnergyStats[] stats = new EnergyStats[perSockEner.length];
		for (int i = 0; i < perSockEner.length; i++) {
			String[] statsStrings = perSockEner[i].split(","); 
			double[] statsNums = new double[statsStrings.length];
			for (int _i = 0; _i < statsNums.length; _i++)
				statsNums[_i] = Double.parseDouble(statsStrings[_i]);

			int socket = i+1;
			stats[i] = new EnergyStats(socket, statsNums, null);
		}
		return stats;
	}

}
