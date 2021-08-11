package jRAPL;
import java.util.Arrays;
import java.time.Instant;
import java.time.Duration;
public class EnergyMonitor extends EnergyManager {
@Override
public void activate()
@Override
public void deactivate()
native static String energyStatCheck();
private static double[] stringArrayToDoubleArray(String[] s)
protected static double[] stringToPrimitiveSample(String energyString)
protected static EnergyStats stringToEnergyStats(String energyString)
protected static double[] subtractPrimitiveSamples(double[] a, double[] b)
}
