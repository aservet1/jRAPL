package jrapl;

import java.util.Arrays;

/**
*	Ways to regulate computer's energy consumption. This class is currently just a dump of native methods
*	that I have never used and seem to be under this category, but I have very little undersanding and
*	have only skimmed over their C side definitions. The API for this class will change a lot as
*	I actually start making use of these functions and understanding them and their place better.
*/
public class EnergyControlUtils extends JRAPL {

	private EnergyControlUtils() {} // private constructor -- never initialized

	/** Defined in <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>CScaler.c</a> for Kenan's EnergyAwareJVM project */
	public native static int scale(int freq);

	/** Defined in <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>CScaler.c</a> for Kenan's EnergyAwareJVM project */
	public native static int[] freqAvailable();

	/** Defined in <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>CScaler.c</a> for Kenan's EnergyAwareJVM project */
	public native static double[] GetDramPowerSpec();
	
	/** Defined in <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>CScaler.c</a> for Kenan's EnergyAwareJVM project */
	public native static void SetPackagePowerLimit(int socketId, int level, double costomPower);

	/** Defined in <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>CScaler.c</a> for Kenan's EnergyAwareJVM project */
	public native static void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin);

	/** Defined in <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>CScaler.c</a> for Kenan's EnergyAwareJVM project */
	public native static void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);

	/** Defined in <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>CScaler.c</a> for Kenan's EnergyAwareJVM project */
	public native static void SetDramPowerLimit(int socketId, int level, double costomPower);

	/** Defined in <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>CScaler.c</a> for Kenan's EnergyAwareJVM project */
	public native static void SetPowerLimit(int ENABLE);
}
