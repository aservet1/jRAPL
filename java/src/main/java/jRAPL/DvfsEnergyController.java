
package jRAPL;

/** 
*	Ways to regulate computer's energy with DVFS. This class is currently just a dump of native methods
*	that I have never used and seem to be under this category, but I have very little undersanding and
*	have only skimmed over their C side definitions. The API for this class will change a lot as
*	I actually start making use of these functions and understanding them and their place better.
*/
public class DvfsEnergyController extends EnergyController {

	/** Defined in <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>CScaler.c</a> for Kenan's EnergyAwareJVM project */
	public native static int scale(int freq);

	/** Defined in <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>CScaler.c</a> for Kenan's EnergyAwareJVM project */
	public native static int[] freqAvailable();

	public native static void SetGovernor(String gov);
}
