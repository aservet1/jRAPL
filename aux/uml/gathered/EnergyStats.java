package jRAPL;
import java.time.Instant;
public final class EnergyStats extends EnergySample {
private Instant timestamp;
public EnergyStats(double[] primitiveSample, Instant ts)
public EnergyStats(EnergyStats other)
public static String csvHeader()
@Override
public String csv()
public Instant getTimestamp()
public void setTimestamp(Instant ts)
}
