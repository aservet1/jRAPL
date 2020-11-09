
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

	public EnergyStats getObjectSample(int socket)
	{ 
		String energyString = EnergyMonitor.energyStatCheck(socket);
		Instant birthday = Instant.now();
		double[] statsArray = EnergyStringParser.toPrimitiveArray(energyString);
		return new EnergyStats(socket, statsArray, birthday);
	}

	public EnergyStats[] getObjectSample()
	{
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
	{ 
		String energyString = EnergyMonitor.energyStatCheck(socket);
		return EnergyStringParser.toPrimitiveArray(energyString);
	}

	public static void main(String[] args) throws InterruptedException
	{
		SyncEnergyMonitor monitor = new SyncEnergyMonitor();
		monitor.init();

		for (int n = 0; n < 1000; n++) {
			EnergyStats[] a = monitor.getObjectSample();
			System.out.println(Arrays.toString(a));
			Thread.sleep(47);
		}

		//int socket = 1;
		//EnergyStats before = monitor.getObjectSample(socket);
		//EnergyStats after;
		//EnergyDiff diff;
		//for (int i = 0; i < 1000; i++) {
		//	try { Thread.sleep(40); }
		//	catch (Exception e) { e.printStackTrace(); }
		//	after = monitor.getObjectSample(socket);
		//	diff = EnergyDiff.between(before, after);
		//	System.out.println(diff.dump());
		//	before = after;
		//}

		monitor.dealloc();
	}

}






















