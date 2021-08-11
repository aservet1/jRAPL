package jRAPL;
import java.time.Instant;
public class AsyncEnergyMonitorCSide extends AsyncEnergyMonitor
{
private native static void startNative();
private native static void stopNative();
private native static void resetNative();
private native static void activateNative(int samplingRate,int storageType,int size_parameter);
private native static void deactivateNative();
private native static void writeFileCSVNative(String filePath);
private native static String getLastKSamplesNative(int k);
private native static long[] getLastKTimestampsNative(int k);
private native static int getNumSamplesNative();
private native static void setSamplingRateNative(int s);
private native static int getSamplingRateNative();
private static final int DYNAMIC_ARRAY_STORAGE = 1;
private static final int LINKED_LIST_STORAGE = 2;
private int samplingRate;
private int storageType;
private int initialSize = 128;
public AsyncEnergyMonitorCSide()
public AsyncEnergyMonitorCSide(int s, String storageTypeString, int size)
public AsyncEnergyMonitorCSide(String storageTypeString)
@Override
public void activate()
@Override
public void deactivate()
@Override
public void start()
@Override
public void stop()
@Override
public void writeFileCSV(String filePath)
@Override
public String[] getLastKSamples(int k)
@Override
public Instant[] getLastKTimestamps(int k)
@Override
public int getNumSamples()
@Override
public int getSamplingRate()
@Override
public void setSamplingRate(int s)
@Override
public void reset()
}
