package jRAPL;
import java.time.Duration;
public final class EnergyDiff extends EnergySample {
private Duration elapsedTime = null;
public EnergyDiff(double[] primitiveSample, Duration elapsedTime)
public void setElapsedTime(Duration elapsed)
public Duration getElapsedTime()
public static String csvHeader()
@Override
public String csv()
public static EnergyDiff between(EnergyStats before, EnergyStats after)
}
