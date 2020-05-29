package jrapl;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;


/**
*	@author Alejandro Servetto
*	Object that takes and stores energy readings over a set delay (milliseconds)
*	Runs as a thread in the background between this.startReading() and this.stopReading()
*	Every individual energy reading is the energy consumed (joules) over the course of the delay
*	Energy read from three power domains: DRAM, CPU core, CPU package
*/
public class EnergyReadingCollector implements Runnable
{
	private ArrayList<double[]> readings; 
	private int delay; // milliseconds
	private volatile boolean exit = false;
	private Thread t = null;

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

	private String labelledReading(double[] reading)
	{
		return "dram: " + reading[0] + "\tcore: " + reading[1] + "\tpkg: " + reading[2];
	}

	public void run()
	{
		while (!exit)
		{
			double[] reading = readOverDelay();
			readings.add(reading);
		}
	}

	/**
	*	Starts a thread with this object. Continually takes and stores energy readings
	*	in the background while main thread runs. Will run until this.stopReading() is called
	*/
	public void startReading()
	{
		t = new Thread(this);
		t.start();
	}

	/**
	*	Stops the thread this object is running. Sets the 'exit' flag to true,
	*	which ends the loop in the run() method.
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
	*	Call this if you intend to reuse the same object for energy collection after already using it.
	*	Clears out the current list of readings stored in the object.
	*/
	public void reInit()
	{
		exit = false;
		readings.clear();
	}

	/**
	*	Returns K most recent readings. Each readings is a double[] of the form
	*	[dram energy, core energy, package energy]. If K is greater than the amount
	*	of readings, simply returns all readings
	*	@param k: Number of most recent readings
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
	*	@return The current delay (in milliseconds)
	*/
	public int getDelay()
	{
		return delay;
	}

	/**
	*	@param d: Sets the delay for this object (in milliseconds)
	*/
	public void setDelay(int d)
	{
		delay = d;
	}

	/**
	*	@return number of readings currently stored in the object
	*/
	public int getNumReadings()
	{
		return readings.size();
	}

	/**
	*	Dumps all readings to file, along with the delay between readings. Format:
	*	Same format as toString(). @see toString()
	*	
	*	@param fileName: name of file to write to
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
	*	Returns a string representation of the object
	*	Format:
	*	delay: (ms)
	*	dram: (joules)	core: (joules)	pkg: (joules)
	*	dram: (joules)	core: (joules)	pkg: (joules)
	*	dram: (joules)	core: (joules)	pkg: (joules)
	*	dram: (joules)	core: (joules)	pkg: (joules)
	*		... et cetera ...
	*	@return string representation of the object
	*/
	public String toString()
	{
		String s = "";
		s += "delay: " + delay + " milliseconds\treadings: " + readings.size() + "\n";
		for (double[] reading : readings)
			s += labelledReading(reading) + "\n";
		return s;
	}

}
