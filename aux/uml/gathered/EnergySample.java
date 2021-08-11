package jRAPL;
import java.util.Arrays;
public abstract class EnergySample
{
private final double[] primitiveSample;
protected native static String csvHeader();
public EnergySample(double[] primitiveSample)
public EnergySample(EnergySample other)
protected String csv()
public double[] getPrimitiveSample()
private double getEnergy(int socket, int index)
public double getCore(int socket)
public double getCore()
public double getGpu(int socket)
public double getGpu()
public double getPackage(int socket)
public double getPackage()
public double getDram(int socket)
public double getDram()
}
