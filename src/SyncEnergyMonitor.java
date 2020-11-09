
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
	{ 
		String energyString = EnergyMonitor.energyStatCheck(socket);
		Instant birthday = Instant.now();
		double[] statsArray = EnergyStringParser.toPrimitiveArray(energyString);
		return new EnergyStats(socket, statsArray, birthday);
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
		String energyString = EnergyMonitor.energyStatCheck(0);
		Instant birthday = Instant.now();
		EnergyStats[] objects = EnergyStringParser.toObjectArray(energyString);
		for (EnergyStats e : objects) e.setTimestamp(birthday);
		return objects;
	}

	public double[] getPrimitiveSample()
	{
		String energyString = EnergyMonitor.energyStatCheck(0);
		return EnergyStringParser.toPrimitiveArray(energyString);
	}
	
	public double[] getPrimitiveSample(int socket)
	{ //TODO make a more extensive "target-one-socket-at-a-time" implementation on the C side
	  // instead of reading all of the sockets and just picking out the ones that you want.
	  // more efficient that way; no unnecessary sockets accessed
		//int lo = socket-1;
		//int hi = lo + ArchSpec.NUM_SOCKETS;
		
		String energyString = EnergyMonitor.energyStatCheck(socket);
		//double[] energyArray = EnergyStringParser.toPrimitiveArray(energyString);
		return EnergyStringParser.toPrimitiveArray(energyString); //Arrays.copyOfRange(energyArray,lo,hi);
	}

	public static void main(String[] args) throws InterruptedException
	{
		SyncEnergyMonitor monitor = new SyncEnergyMonitor();
		monitor.init();

		for (int i = 0; i < 1000; i++) {
			double[] sample = monitor.getPrimitiveSample(1);
			System.out.println(Arrays.toString(sample));
			Thread.sleep(40);
		}

		//EnergyStats before = monitor.getObjectSample(1);
		//EnergyStats after;
		//EnergyDiff diff;
		//for (int i = 0; i < 1000; i++) {
		//	try { Thread.sleep(40); }
		//	catch (Exception e) { e.printStackTrace(); }
		//	after = monitor.getObjectSample(1);
		//	diff = EnergyDiff.between(before, after);
		//	System.out.println(diff.dump());
		//	before = after;
		//}

		monitor.dealloc();
	}

}






















