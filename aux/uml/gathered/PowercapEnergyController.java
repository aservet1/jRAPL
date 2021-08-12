package jRAPL
PowercapEnergyController extends EnergyController {
+ GetDramPowerSpec()   double[] 
+ SetPackagePowerLimit(int socketId, int level, double costomPower)   void 
+ SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin)   void 
+ SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin)   void 
+ SetDramPowerLimit(int socketId, int level, double costomPower)   void 
+ SetPowerLimit(int ENABLE)   void 
}
