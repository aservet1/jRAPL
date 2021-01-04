
package jrapl;

import java.time.Instant;
import java.time.Duration;

public abstract class AsyncEnergyMonitor extends EnergyMonitor {

	protected Instant monitorStartTime = null;
	protected Instant monitorStopTime = null;
	public Duration getLifetime()
	{
		if (monitorStartTime != null && monitorStopTime != null)
			return Duration.between(monitorStartTime, monitorStopTime);
		else return null;
	}

	public void start()
	{
		monitorStartTime = Instant.now();
	}

	public void stop()
	{
		monitorStopTime = Instant.now();
	}

	public abstract String toString();

	public void reset()
	{
		monitorStartTime = null;
		monitorStopTime = null;
	}

	public abstract void writeToFile(String fileName);

	public abstract String[] getLastKSamples(int k);
	public abstract Instant[] getLastKTimestamps(int k);

	/* Returns an array of arrays of EnergyStats objects. Each individual array
		is a list of the readings for all sockets requested. Even if only one
		socket was read from, it's still an array of arrays. The single socket
		reading is just index 0 of a 1-element array, regardless of whether it's
		just one socket because you asked for a specific socket, or because you
		were reading all sockets but only had one. */
	public EnergyStats[][] getLastKSamples_Objects(int k) 
	{
		String[] strings = getLastKSamples(k);
		Instant[] timestamps = getLastKTimestamps(k);

		EnergyStats[][] samplesArray = new EnergyStats[k][ArchSpec.NUM_SOCKETS*ArchSpec.NUM_STATS_PER_SOCKET];
		for (int i = 0; i < strings.length; i++) {
			String energyString = strings[i];
			samplesArray[i] = EnergyStringParser.toObjectArray(energyString);
			for (EnergyStats e : samplesArray[i]) e.setTimestamp(timestamps[i]);
		}

		return samplesArray;
	}
	public double[][] getLastKSamples_Arrays(int k)
	{
		String[] strings = getLastKSamples(k);
	
		double[][] samplesArray = new double[k][ArchSpec.NUM_SOCKETS*ArchSpec.NUM_STATS_PER_SOCKET];
		for (int i = 0; i < strings.length; i++) {
			String energyString = strings[i];
			samplesArray[i] = EnergyStringParser.toPrimitiveArray(energyString);
		}

		return samplesArray;
	}
}
