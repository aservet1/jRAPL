
package jrapl;

import java.util.Arrays;
import java.time.Instant;

public class SyncEnergyMonitor extends EnergyMonitor {

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public void dealloc()
	{
		super.dealloc();
	}

	public EnergyStats getObjectSample(int socket) // for a specific socket
	{ //TODO make a more extensive "target-one-socket-at-a-time" implementation on the C side
	  // instead of reading all of the sockets and just picking out the ones that you want.
	  // more efficient that way.
		int i = socket-1;
		return getObjectSample()[i];
		
	}

	public EnergyStats[] getObjectSample()
	{
		//EnergyStats[] stats = new EnergyStats[ArchSpec.NUM_SOCKETS];
		//double[] energy = EnergyCheckUtils.getEnergyStats();

		//int lo = 0;
		//int hi = ArchSpec.NUM_STATS_PER_SOCKET;

		//for (int i = 0; i < ArchSpec.NUM_SOCKETS; i++) {
		//	int socket = i+1;
		//	stats[i] = new EnergyStats(socket, Arrays.copyOfRange(energy,lo,hi));
		//	lo += ArchSpec.NUM_STATS_PER_SOCKET;
		//	hi += ArchSpec.NUM_STATS_PER_SOCKET;
		//}
		String energyString = EnergyMonitor.energyStatCheck();
		Instant birthday = Instant.now();
		EnergyStats[] objects = EnergyStringParser.toObjectArray(energyString);
		for (EnergyStats e : objects) e.setTimestamp(birthday);
		return objects;
	}

	public double[] getPrimitiveSample()
	{
		String energyString = EnergyMonitor.energyStatCheck();
		return EnergyStringParser.toPrimitiveArray(energyString);
	}
	
	public double[] getPrimitiveSample(int socket)
	{ //TODO make a more extensive "target-one-socket-at-a-time" implementation on the C side
	  // instead of reading all of the sockets and just picking out the ones that you want.
	  // more efficient that way; no unnecessary sockets accessed
		int lo = socket-1;
		int hi = lo + ArchSpec.NUM_SOCKETS;
		
		String energyString = EnergyMonitor.energyStatCheck();
		double[] energyArray = EnergyStringParser.toPrimitiveArray(energyString);
		return Arrays.copyOfRange(energyArray,lo,hi);
	}

	public static void main(String[] args)
	{
		SyncEnergyMonitor monitor = new SyncEnergyMonitor();
		monitor.init();

		EnergyStats before = monitor.getObjectSample(1);
		EnergyStats after;
		EnergyDiff diff;
		for (int i = 0; i < 1000; i++) {
			try { Thread.sleep(40); }
			catch (Exception e) { e.printStackTrace(); }
			after = monitor.getObjectSample(1);
			diff = EnergyDiff.between(before, after);
			System.out.println(diff.dump());
			before = after;
		}

		monitor.dealloc();
	}

}






















