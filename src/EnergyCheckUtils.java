package jrapl;
import java.util.Arrays;

/** <h1> DOCUMENTATION OUT OF DATE </h1>
*   <b>The goal is to get rid of this class entirely.</b>
*	Functions around getting energy reading of the system
*/
public class EnergyCheckUtils {

	// TODO eventually do away with this method entirely and replace it with a more
	//  appropriate equivalent, but I'm leaving it in here for now to stop the bleeding
	//  when I try to compile without having to change all of its external instances.
	//  but phase out its usage and delete this method definition entirely once you know
	//  that it's been supplanted by whatever other more appropriate methods come in and
	//  take its place
	//
	// the goal is to get rid of this class entirely
	public static double[] getEnergyStats() {
		return EnergyStringParser.toPrimitiveArray(EnergyMonitor.energyStatCheck(0)); // this reading from all sockets
	}

	public static void main(String[] args) throws Exception
	{
		EnergyManager manager = new EnergyManager();
		manager.init();

		for (long x = 0; x < 9837427; x++) {
			System.out.println(Arrays.toString(getEnergyStats()));
			Thread.sleep(40);
		}

		manager.dealloc();
	}

}
