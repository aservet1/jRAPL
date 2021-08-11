package jRAPL;
import java.time.Instant;
import java.time.Duration;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.IOException;
public abstract class AsyncEnergyMonitor extends EnergyMonitor {
protected Instant monitorStartTime = null;
protected Instant monitorStopTime = null;
protected boolean isRunning = false;
protected int samplingRate;
@Override
public  void  activate()
@Override
public void deactivate()
public abstract int getNumSamples();
public abstract void setSamplingRate(int s);
public abstract int getSamplingRate();
public Duration getLifetime()
public void start()
public void stop()
public void reset()
public abstract Instant[] getLastKTimestamps(int k);
public abstract String[] getLastKSamples(int k);
public EnergyStats[] getLastKSamples_Objects(int k)
public double[][] getLastKSamples_Arrays(int k)
public boolean isRunning()
public abstract void writeFileCSV(String fileName);
public void writeFileMetadata(String fileName)
public String toString()
}
