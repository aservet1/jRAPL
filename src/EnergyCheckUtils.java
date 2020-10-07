package jrapl;
import java.util.Arrays;

/** <h1> DOCUMENTATION OUT OF DATE </h1>
*	Functions around getting energy reading of the system
*/
public class EnergyCheckUtils extends JRAPL {

//	private EnergyCheckUtils() {} // private constructor -- never initialized
	
	/** <h1> DOCUMENTATION OUT OF DATE </h1> Returns a string with current total energy consumption reported in MSR registers.
	 *	<br>Formatted " 1stSocketInfo @ 2ndSocketInfo @ ... @ NthSocketInfo " with @ delimiters
	 *	<br>Each NthSocketInfo subsection formatted " dram_energy # cpu_energy # package_energy " with # delimieters
	 *	<br>This string gets parsed into an array in getEnergyStats().
	 *	<br>Example string for a 2-socket machine: 9389.21312#239874.987213#97432.2333@12321.3211#987324.1222#1237.213
	 *	@return String containing per socket energy info
	*/
	public native static String EnergyStatCheck();


	/** <h1> DOCUMENTATION OUT OF DATE </h1> Parses string generated from the native EnergyStatCheck() method into an array of doubles.
	 *  <br>Array will be size (3 * socketnum). There will be three entries per socket
	 *  <br>The first entry is: Dram/uncore gpu energy (depends on the cpu architecture)
	 *  <br>The second entry is: CPU energy
	 *  <br>The third entry is: Package energy
	 *  <br>General layout of the array:
	 * 	[dram_s1, cpu_s1, pkg_s1, dram_s2, cpu_s2, pkg_s2, ... , dram_sn, cpu_sn, pkg_sn]
	 *	sn means socket number associated with this reading for all n greater than 1
	 * @return an array of current energy information.
	*/
	public static double[] getEnergyStats() {
		String EnergyInfo = EnergyStatCheck();
		
		String[] perSockEner = EnergyInfo.split("@");
		double[] stats = new double[4*ArchitectureSpecifications.NUM_SOCKETS]; // 4 stats per socket
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
		JRAPL.loadLibrary();
		JRAPL.ProfileInit();
		for (int x = 0; true; x++){
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
	}

}
