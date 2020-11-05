package jrapl;
import java.util.Arrays;

/** <h1> DOCUMENTATION OUT OF DATE </h1>
*	Functions around getting energy reading of the system
*/
public class EnergyCheckUtils {

	public native static String energyStatCheck();

	public static double[] getEnergyStats() {
		String EnergyInfo = energyStatCheck();
		
		String[] perSockEner = EnergyInfo.split("@");
		double[] stats = new double[4*ArchSpec.NUM_SOCKETS]; // 4 stats per socket
		int count = 0;

		for(int i = 0; i < perSockEner.length; i++) {
			String[] energy = perSockEner[i].split(",");
			for(int j = 0; j < energy.length; j++) {
				count = i * 4 + j;	//accumulative count
		
				stats[count] = Double.parseDouble(energy[j]);
			}
		}
		return stats;
	}

	public static void main(String[] args) throws Exception
	{
		EnergyManager manager = new EnergyManager();
		manager.init();

		for (long x = 0; x < 9837427; x++){
			System.out.println(Arrays.toString(getEnergyStats()));
			Thread.sleep(40);
		}
		/*for(int x = 0; x < 100; x++){
			double[] before = getEnergyStats();
			Thread.sleep(40);
			double[] after = getEnergyStats();
			double[] diff = {after[0]-before[0], after[1]-before[1], after[2]-before[2], after[3]-before[3]};
			System.out.println(String.join(", ",
				"DRAM: " + String.format("%.4f", diff[0]),
				"GPU: " + String.format("%.4f", diff[1]),
				"Core: " + String.format("%.4f", diff[2]),
				"Package: " + String.format("%.4f", diff[3])));

		}*/

		manager.dealloc();
	}

}
