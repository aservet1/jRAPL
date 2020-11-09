package jrapl;

// there's gotta be more to it than this, right?
public class EnergyMonitor extends EnergyManager {

	public void init() {
		super.init();
	}

	public void dealloc() {
		super.dealloc();
	}
	
	public native static String energyStatCheck(int whichSocket);

	// TODO -- decide if these protected 'energyStringTo...' methods
	//  should just be part of an EnergyStringParser class or if
	//  that would just be splitting hairs
	protected static double[] energyStringToArray(String energyString)
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
	protected static EnergyStats[] energyStringToObject(String energyString)
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













