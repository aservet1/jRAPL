package jrapl;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;


public class EnergyReadingCollector implements Runnable
{
	private ArrayList<double[]> readings; //@TODO -- might accidentally pick up an odd reading in the case that the thing stops really quick
	private int delay; // milliseconds
	private volatile boolean exit = false;

	public EnergyReadingCollector()
	{
		delay = 10;
		readings = new ArrayList<double[]>();
	}

	public EnergyReadingCollector(int d)
	{
		delay = d;
		readings = new ArrayList<double[]>();
	}

	private double[] readOverDelay()
	{
		double[] before = EnergyCheckUtils.getEnergyStats();
		try { Thread.sleep(delay); } catch (Exception e) {}
		double[] after  = EnergyCheckUtils.getEnergyStats();
		double[] reading = new double[3];
		for (int i = 0; i < reading.length; i++){
			reading[i] = after[i]-before[i];
		}
		return reading;
	}

	public void run()
	{
		while (!exit)
		{
			double[] reading = readOverDelay();
			if (!exit) readings.add(reading); // seems redundant, but this is in case the exit signal was set during the readOverDelay() signal --(is this OK?)
		}
	}

	public void startReading()
	{
		new Thread(this).start();
	}

	public void stopReading()
	{
		exit = true;
	}

	// run this before starting a thread on the same object for the second time
	public void reInit()
	{
		exit = false;
		readings.clear();
	}

	public double[][] getLastKReadings(int k)
	{
		double[][] readings_array = new double[k][];

		int start = readings.size() - k;
		int array_index = 0;

		if (start < 0) start = 0;

		for (int i = start; i < readings.size(); i++)
			readings_array[array_index++] = readings.get(i);
		return readings_array;
	}

	private String labelledReading(double[] reading)
	{
		return "dram: " + reading[0] + "\tcpu: " + reading[1] + "\tpkg: " + reading[2];
	}

	//dump to file
	public void writeReadingsToFile(String fileName)
	{
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(fileName));
			writer.write(this.toString());
			writer.close();
		} catch (IOException e) {
			System.out.println("error writing " + fileName);
			e.printStackTrace();
		}
	}

	public int getDelay()
	{
		return delay;
	}

	public void setDelay(int d)
	{
		delay = d;
	}

	public String toString()
	{
		String s = "";
		s += "delay: " + delay + '\n';
		if (readings.size() != 0) {
			int i;
			for (double[] reading : readings) {
					s += labelledReading(reading) + "\n";
			}
		}
		return s;
	}

}
