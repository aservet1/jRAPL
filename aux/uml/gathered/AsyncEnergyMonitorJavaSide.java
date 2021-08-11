package jRAPL;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileWriter;
import java.time.Instant;
public class AsyncEnergyMonitorJavaSide extends AsyncEnergyMonitor implements Runnable {
private ArrayList<String> samples;
private int samplingRate;
private volatile boolean exit = false;
private Thread t = null;
public AsyncEnergyMonitorJavaSide()
public AsyncEnergyMonitorJavaSide(int s)
@Override
public void activate()
@Override
public void deactivate()
public void run()
@Override
public void start()
@Override
public void stop()
@Override
public void reset()
@Override
public String[] getLastKSamples(int k)
@Override
public Instant[] getLastKTimestamps(int k)
@Override
public int getSamplingRate()
@Override
public void setSamplingRate(int s)
@Override
public int getNumSamples()
@Override
public void writeFileCSV(String fileName)
}
