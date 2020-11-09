
package jrapl;

import java.time.Instant;

public abstract class AsyncEnergyMonitor extends EnergyMonitor {

	public abstract void start();

	public abstract void stop();

	public abstract String toString();

	public abstract void reset();

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
