package jRAPL;
public class DvfsEnergyController extends EnergyController {
public native static int scale(int freq);
public native static int[] freqAvailable();
public native static void SetGovernor(String gov);
}
