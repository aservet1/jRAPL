package jRAPL;
public class PowercapEnergyController extends EnergyController {
public native static double[] GetDramPowerSpec();
public native static void SetPackagePowerLimit(int socketId, int level, double costomPower);
public native static void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin);
public native static void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);
public native static void SetDramPowerLimit(int socketId, int level, double costomPower);
public native static void SetPowerLimit(int ENABLE);
}
