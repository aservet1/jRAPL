package jrapl;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

import java.time.Instant;

public class AsyncEnergyMonitorJavaSide extends AsyncEnergyMonitor implements Runnable
{
	private ArrayList<String> samples; 
	private int samplingRate; // milliseconds
	private volatile boolean exit = false;
	private Thread t = null;

	protected final ArrayList<Instant> timestamps; //TODO -- decide if you want to have a boolean that enables whether or not you do want to collect timestamps

	/** <h1> DOCUMENTATION OUT OF DATE </h1> Initializes sample collector with a default sampling rate setting of 10 milliseconds */
	public AsyncEnergyMonitorJavaSide()
	{
		samplingRate = 10;
		timestamps = new ArrayList<Instant>();
		samples = new ArrayList<String>();
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Initializes sample collector with the sampling rate passed as paramter
	*	@param s The sampling rate over which to take samples (in milliseconds)
	*/
	public AsyncEnergyMonitorJavaSide(int s)
	{
		samplingRate = s;
		timestamps = new ArrayList<Instant>();
		samples = new ArrayList<String>();
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
		while (!exit) {
			String energyString = EnergyMonitor.energyStatCheck();
			samples.add(energyString);
			timestamps.add(Instant.now());
			try { Thread.sleep(samplingRate); } catch (Exception e) {}
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
	public void reset()
	{
		exit = false;
		samples.clear();
		timestamps.clear();
	}




	////////////////////////////////////////////////////////////// do these with the parse_ener_string function /////////////////////////////////////////////////////////
//	private double[] stringToArray(String energyString) {
//		String[] perSocket = energyString.split("@");
//		double[] result;
//		for (String sample : perSocket) {
//			String[] 
//		}
//	}
//
//
//	public EnergyStats[] getLastKSamples(int k, String dataType)
//	{
//		int start = samples.size() - k;
//		int arrayIndex = 0;
//
//		if (start < 0) {
//			start = 0;
//			k = samples.size();
//		}
//
//		switch (dataType) {
//			case "STRING":
//				String[] sampleArray = new String[k];
//				break;
//			case "OBJECT":
//				EnergyStats[] sampleArray = new EnergyStats[k];
//				break;
//			case "ARRAY":
//				double[][] sampleArray = new double[][k];
//				for (int i = start; i < samples.size(); i++) {
//					String energyString = samples.get(i);
//					double[][] perSocketSamples = stringToArrays(energyString);
//					for (int _i = 0; _i < perSocketEnergy.length; _i++) {
//						sampleArray[_i++]
//					}
//
//					EnergyStats e = new EnergyStats()
//				}
//				break;
//			default:
//				System.err.println("error: Invalid dataType requested from getLastKSamples: \'"+dataType+"\'");
//				System.exit(1);
//		}
//		
//			String[] samples_array = new String[k];
//
//			for (int i = start; i < samples.size(); i++)
//				samplesArray[arrayIndex++] = samples.get(i);
//
//		return null;
//		return samplesArray;
//	}
//	public double[] getLastKSamples_Arrays(int k)
//	{
//		int start = samples.size() - k;
//		int arrayIndex = 0;
//
//		if (start < 0) {
//			start = 0;
//			k = samples.size();
//		}
//		
//		String[] samples_array = new String[k];
//
//		for (int i = start; i < samples.size(); i++)
//			samplesArray[arrayIndex++] = samples.get(i);
//		return null;
//		return samplesArray;
//	}
//	public String[] getLastKSamples_RawString(int k)
//	{
//		int start = samples.size() - k;
//		int arrayIndex = 0;
//
//		if (start < 0) {
//			start = 0;
//			k = samples.size();
//		}
//		
//		String[] samples_array = new String[k];
//
//		for (int i = start; i < samples.size(); i++)
//			samplesArray[arrayIndex++] = samples.get(i);
//		return samplesArray;
//	}
//
//	public Instant[] getLastKTimestamps(int k)
//	{
//		int start = timestamps.size() - k;
//		int arrayIndex = 0;
//		if (start < 0) {
//			start = 0;
//			k = timestamps.size();
//		}
//
//		Instant[] timestampsArray = new Instarnt[k];
//
//		for (int i = start; i < timstamps.size(); i++)
//			timestampsArray[arrayIndex++] = timestamps.get(i);
//		return timestampsArray;
//
//	}

	////////////////////////////////////////////////////////////// do these with the parse_ener_string function /////////////////////////////////////////////////////////


	


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
	*	<br>  samplingRate: xxx milliseconds
	*	<br>  socket,dram,gpu,core,pkg
	*	<br>  x,xxx,xxx,xxx,xxx
	*	@return CSV version of the data stored in the object
	*/
	public String toString()
	{
		String s = "";
		s += "samplingRate: " + samplingRate + " milliseconds\n";
		s += "socket,dram,gpu,core,pkg,timestamp,elapsed-time ______ INACCURATE HEADER\n";
		for (String sampleString : samples) {
			String[] perSocketStrings = sampleString.split("@");
			for (int i = 0; i < perSocketStrings.length; i++) {
				int socket = i+1;
				s += Integer.toString(socket) + "," + perSocketStrings[i] + "\n";
			}

			//s += sample + "\n";
		}
		return s;
	}

	public static void main(String[] args) throws InterruptedException
	{
		int rate = (args.length > 0) ? Integer.parseInt(args[0]) : 10;
		AsyncEnergyMonitorJavaSide aemonj = new AsyncEnergyMonitorJavaSide(rate);
		aemonj.init();	
	
		aemonj.start();
		Thread.sleep(3000);
		aemonj.stop();

		System.out.println(aemonj);

		aemonj.dealloc();
	}
	
}
