package jrapl;

import java.util.Arrays;

/**
*	Ways to control computer's energy consumption
*/
public class EnergyControlUtils extends JRAPL {

	/** Seems to be defined in Kenan's CScaler.c for EnergyAwareJVM project <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>here</a> */
	public native static int scale(int freq);

	/** Seems to be defined in Kenan's CScaler.c for EnergyAwareJVM project <a href='https://github.com/kliu20/EnergyAwareJVM/blob/master/energy/CScaler.c'>here</a> */
	public native static int[] freqAvailable();

	/** Not fully implemented yet; see msr.c
	*
	*/	
	public native static double[] GetPackagePowerSpec();        // msr.c -- getPowerSpec() with parameter specified for domain (gotta wrap it up)
	
	/** Not fully implemented yet; see msr.c
	*
	*/
	public native static double[] GetDramPowerSpec();           // msr.c -- getPowerSpec() with parameter specified for domain (gotta wrap it up)
	
	/** Not fully implemented yet; see msr.c
	*
	*/
	public native static void SetPackagePowerLimit(int socketId, int level, double costomPower);

	/** Not fully implemented yet; see msr.c
	*
	*/
	public native static void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin);

	/** Not fully implemented yet; see msr.c
	*
	*/
	public native static void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);

	/** Not fully implemented yet; see msr.c
	*
	*/
	public native static void SetDramPowerLimit(int socketId, int level, double costomPower);

	/** Not fully implemented yet; see msr.c
	*
	*/
	public native static void SetPowerLimit(int ENABLE);
}
