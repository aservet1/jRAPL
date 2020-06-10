package jrapl;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

/**
*	Reads and stores sytem energy consumption in a background thread.
*	<br>Meant to record the progression of energy consumption of a program run in the main thread.
*	<br>Spawns a therad between <code>this.startReading()</code> and <code>this.stopReading()</code>.
*	<br>Every individual energy reading is the energy consumed (joules) over the course of a set millisecond delay
*	<br>Energy read from three power domains: DRAM or GPU (depending on CPU model), CPU core, CPU package
*/
public class EnergyReadingCollector extends JRAPL implements Runnable
{
	private ArrayList<double[]> readings; 
	private int delay; // milliseconds
	private volatile boolean exit = false;
	private Thread t = null;
	private String powerDomain1;

	/** Initializes reading collector with a default delay setting of 10 milliseconds */
	public EnergyReadingCollector()
	{
		delay = 10;
		readings = new ArrayList<double[]>();
		switch (ArchSpec.DramOrGpu()) {
			case 1: powerDomain1 = "dram"; break;
			case 2: powerDomain1 = "gpu"; break;
			default:
				System.out.println("ERROR: Your CPU model is not supported!");
				System.exit(1);
		}
	}

	/**
	*	Initializes reading collector with the delay interval passed as paramter
	*	@param d The delay interval over which to take readings (in milliseconds)
	*/
	public EnergyReadingCollector(int d)
	{
		delay = d;
		readings = new ArrayList<double[]>();
		switch (ArchSpec.DramOrGpu()) {
			case 1: powerDomain1 = "dram"; break;
			case 2: powerDomain1 = "gpu"; break;
			default:
				System.out.println("ERROR: Your CPU model is not supported!");
				System.exit(1);
		}
	}

	/**
	*	Do not call this directly from the main thread. 
	*	It is called and run internally by the Thread class via <code>this.startReading()</code>.
	*	Runs a loop, continually reading energy consumption over 
	*	the delay interval and stores the reading. Loop is controlled by an internal boolean,
	*	which is set to stop once the main thread calls <code>this.stopReading()</code>
	*/	
	public void run()
	{
		while (!exit)
		{
			double[] reading = readOverDelay();
			readings.add(reading);
		}
	}

	/**
	*	Starts collecting and storing energy readings in a separate thread. Continually takes and stores energy readings
	*	in the background while main thread runs. Will run until main thread calls <code>this.stopReading()</code>.
	*/
	public void startReading()
	{
		t = new Thread(this);
		t.start();
	}

	/**
	*	Stops collecting and storing energy readings.
	*/
	public void stopReading()
	{
		exit = true;
		try {
			 t.join();
		} catch (Exception e) {
			System.out.println("Exception " + e + " caught.");
			e.printStackTrace();
		}
		t = null;
	}

	/**
	*	Resets the object for reuse.
	*	<br>Call this if you intend to reuse the same object for energy collection after already using it.
	*	<br>Clears out the current list of readings stored in the object.
	*/
	public void reInit()
	{
		exit = false;
		readings.clear();
	}

	/**
	*	Returns K most recent stored readings. Each readings is a double[] of the form
	*	<br>[dram/gpu energy, core energy, package energy].
	*	<br>If K is greater than the amount of readings, returns all readings
	*	@param k Number of most recent readings
	*	@return An array of the K most recent readings.
	*/
	public double[][] getLastKReadings(int k)
	{
		int start = readings.size() - k;
		int array_index = 0;

		if (start < 0) {
			start = 0;
			k = readings.size();
		}
		
		double[][] readings_array = new double[k][];

		for (int i = start; i < readings.size(); i++)
			readings_array[array_index++] = readings.get(i);
		return readings_array;
	}
	
	/**
	*	Gets the delay interval over which the object takes readings.
	*	@return The delay interval (in milliseconds)
	*/
	public int getDelay()
	{
		return delay;
	}

	/**
	*	Sets the delay interval over which to take readings
	*	@param d delay interval (in milliseconds)
	*/
	public void setDelay(int d)
	{
		delay = d;
	}

	/**
	*	Gets the number of readings the object has currently collected
	*	@return number of readings collected so far
	*/
	public int getNumReadings()
	{
		return readings.size();
	}

	/**
	*	Dumps all readings to file, along with the delay between readings.
	*	Same format as <code>this.toString()</code>
	*	
	*	@param fileName name of file to write to
	*/
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

	/**
	*	Human readable format of all data collected, as well all the delay interval over which the data was read.
	*	<br>Format:
	*	<br>  delay: (ms)
	*	<br>  dram/gpu: (joules)	core: (joules)	pkg: (joules)
	*	<br>  dram/gpu: (joules)	core: (joules)	pkg: (joules)
	*	<br>  dram/gpu: (joules)	core: (joules)	pkg: (joules)
	*	<br>  dram/gpu: (joules)	core: (joules)	pkg: (joules)
	*	<br>	... et cetera ...
	*	<br>  note that only one of "dram" and "gpu" will be listed for the first column, depending on your CPU model
		<br>  each entry per line is tab delimited
	*	@return Human readable interpretation of the data stored in the object
	*/
	public String toString()
	{
		String s = "";
		s += "delay: " + delay + " milliseconds";
		for (double[] reading : readings)
			s += labelledReading(reading) + "\n";
		return s;
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

	private String labelledReading(double[] reading)
	{
		return powerDomain1 + " " + reading[0] + "\tcore: " + reading[1] + "\tpkg: " + reading[2];
	}

}
