package jrapl;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class AsyncEnergyMonitorJavaSide extends JRAPL implements Runnable,AsyncMonitor
{
	private ArrayList<double[]> samples; 
	private int samplingRate; // milliseconds
	private volatile boolean exit = false;
	private Thread t = null;

	/** <h1> DOCUMENTATION OUT OF DATE </h1> Initializes sample collector with a default sampling rate setting of 10 milliseconds */
	public AsyncEnergyMonitorJavaSide()
	{
		samplingRate = 10;
		samples = new ArrayList<double[]>();
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Initializes sample collector with the sampling rate passed as paramter
	*	@param s The sampling rate over which to take samples (in milliseconds)
	*/
	public AsyncEnergyMonitorJavaSide(int s)
	{
		samplingRate = s;
		samples = new ArrayList<double[]>();
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Do not call this directly from the main thread. 
	*	It is called and run internally by the Thread class via <code>this.start()</code>.
	*	Runs a loop, continually reading energy consumption over 
	*	the samplingRate and stores the sample. Loop is controlled by an internal boolean,
	*	which is set to stop once the main thread calls <code>this.stop()</code>
	*/	
	public void run()
	{
		while (!exit)
		{
			double[] stats = EnergyCheckUtils.getEnergyStats();
			samples.add(stats);
			try { Thread.sleep(samplingRate); }
			catch (Exception e) {} //park support or lock support
		}
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Starts collecting and storing energy samples in a separate thread. Continually takes and stores energy samples
	*	in the background while main thread runs. Will run until main thread calls <code>this.stop()</code>.
	*/
	public void start()
	{
		t = new Thread(this);
		t.start();
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Stops collecting and storing energy samples.
	*/
	public void stop()
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

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Resets the object for reuse.
	*	<br>Call this if you intend to reuse the same object for energy collection after already using it.
	*	<br>Clears out the current list of samples stored in the object.
	*/
	public void reInit()
	{
		exit = false;
		samples.clear();
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Returns K most recent stored samples. Each samples is a T of the form
	*	<br>[dram/gpu energy, core energy, package energy].
	*	<br>If K is greater than the amount of samples, returns all samples
	*	@param k Number of most recent samples
	*	@return An array of the K most recent samples.
	*/
	public double[][] getLastKSamples(int k)
	{
		int start = samples.size() - k;
		int array_index = 0;

		if (start < 0) {
			start = 0;
			k = samples.size();
		}
		
		double[][] samples_array = new double[k][];

		for (int i = start; i < samples.size(); i++)
			samples_array[array_index++] = samples.get(i);
		return samples_array;
	}
	
	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Gets the sampling rate for the thread to collect samples.
	*	@return The sampling rate (in milliseconds)
	*/
	public int getSamplingRate()
	{
		return samplingRate;
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Sets the sampling rate over which to take samples
	*	@param s sampling rate (in milliseconds)
	*/
	public void setSamplingRate(int s)
	{
		samplingRate = s;
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Gets the number of samples the object has currently collected
	*	@return number of samples collected so far
	*/
	public int getNumReadings()
	{
		return samples.size();
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Dumps all samples to file, along with the sampling rate.
	*	Same format as <code>this.toString()</code>
	*	
	*	@param fileName name of file to write to
	*/
	public void writeToFile(String fileName)
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

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	CSV format of all data collected. First two lines are the sampling rate and a header describing which power domain
	*	each column's energy samples represent
	*	<br>Format:
	*	<br>  samplingRate: xxx (ms)
	*	<br>  socket,dram,gpu,cpu,pkg
	*	<br>  x,xxx,xxx,xxx,xxx
	*	<br>  x,xxx,xxx,xxx,xxx
	*	<br>  x,xxx,xxx,xxx,xxx
	*	<br>  x,xxx,xxx,xxx,xxx
	*	<br>	... et cetera ...
	*	<br>  note that only one of "dram" and "gpu" will be listed for the first column, depending on your CPU model
		<br>  each entry per line is tab delimited
	*	@return Human readable interpretation of the data stored in the object
	*/
	public String toString()
	{
		String s = "";
		s += "samplingRate: " + samplingRate + " milliseconds\n";
		s += "socket,dram,gpu,cpu,pkg,timestamp,elapsed-time\n"; //this header is almost /definitely/ incorrect
		for (double[] sample : samples)
			s += commaSeparated(sample) + "\n";
		return s;
	}
	
	private String commaSeparated(double[] sample)
	{
		String s = new String();
		for (int i = 0; i < sample.length-1; i++)
			s += String.format("%.4f", sample[i]) + ",";
		s += String.format("%.4f", sample[sample.length-1]);
		return s;
	}

	/*private double[] readSample()
	{
		double[] before = EnergyCheckUtils.getEnergyStats();
		try { double[]hread.sleep(samplingRate); } catch (Exception e) {} //park support or lock support
		double[] after  = EnergyCheckUtils.getEnergyStats();
		double[] sample = new double[after.length];
		for (int i = 0; i < sample.length; i++){
			sample[i] = after[i] - before[i];
		}
		return sample;
	}*/

}
