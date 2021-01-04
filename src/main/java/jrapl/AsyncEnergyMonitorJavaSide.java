package jrapl;

import java.util.Arrays;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileWriter;

import java.time.Instant;
import java.time.Duration;

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
			String energyString = EnergyMonitor.energyStatCheck(0);
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
		super.start();
		t = new Thread(this);
		t.start();
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*	Stops collecting and storing energy samples.
	*/
	public void stop()
	{
		super.stop();
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
		super.reset();
		exit = false;
		samples.clear();
		timestamps.clear();
	}

	public String[] getLastKSamples(int k) 
	{
		int start = samples.size() - k;
		int arrayIndex = 0;

		if (start < 0) {
			start = 0;
			k = samples.size();
		}
		
		String[] samplesArray = new String[k];
		for (int i = start; i < samples.size(); i++)
			samplesArray[arrayIndex++] = samples.get(i);
		
		return samplesArray;
	}

	public Instant[] getLastKTimestamps(int k) 
	{
		int start = timestamps.size() - k;
		int arrayIndex = 0;
		if (start < 0) {
			start = 0;
			k = timestamps.size();
		}

		Instant[] timestampsArray = new Instant[k];

		for (int i = start; i < timestamps.size(); i++)
			timestampsArray[arrayIndex++] = timestamps.get(i);

		return timestampsArray;

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
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter ( // write to stdout if filename is null
									(fileName == null)
										? new OutputStreamWriter(System.out)
										: new FileWriter(new File(fileName))
									);

			writer.write("samplingRate: " + samplingRate + " milliseconds\n");
			writer.write("socket,"+ArchSpec.ENERGY_STATS_STRING_FORMAT.split("@")[0]+",timestamp(usec since epoch)\n");
			for (int i = 0; i < samples.size(); i++) {
				String energyString = samples.get(i);
				String[] perSocketStrings = energyString.split("@");
				long usecs = Duration.between(Instant.EPOCH, timestamps.get(i)).toNanos()/1000;
				for (int _i = 0; _i < perSocketStrings.length; _i++) {
					int socket = _i+1;
					writer.write(
						Integer.toString(socket) + "," 
						+ perSocketStrings[_i] + "," 
						+ Long.toString(usecs) + "\n"
					);
				}
			}
			writer.flush();
			if (fileName != null)
				writer.close(); // only close if you were writing to an actual file, otherwise you would be closing System.out
		} catch (IOException e) {
			System.out.println("error writing " + fileName);
			e.printStackTrace();
		}
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	*/
	public String toString()
	{
		String s = "";
		s += "samplingRate: " + samplingRate + " milliseconds\n";
		s += "lifetime: " + Long.toString(getLifetime().toMillis()) + " milliseconds\n";
		s += "number of samples: " + Integer.toString(getNumReadings()) + "\n";

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
		//aemonj.writeToFile("tmep");
		int k = 5;
		System.out.println(Arrays.deepToString(aemonj.getLastKSamples_Arrays(k)));
		System.out.println();
		System.out.println(Arrays.toString(aemonj.getLastKTimestamps(k)));

		aemonj.dealloc();
	}
	
}
