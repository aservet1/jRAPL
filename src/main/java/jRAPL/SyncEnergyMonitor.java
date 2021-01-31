
package jRAPL;

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

	public EnergyStats getSample(int socket)
	{ 
		String energyString = EnergyMonitor.energyStatCheck(socket);
		Instant birthday = Instant.now();
		double[] statsArray = Utils.stringToPrimitiveArray(energyString);
		return new EnergyStats(socket, statsArray, birthday);
	}

	public EnergyStats[] getSample()
	{
		String energyString = EnergyMonitor.energyStatCheck(0);
		Instant birthday = Instant.now();
		EnergyStats[] objects = Utils.stringToObjectArray(energyString);
		for (EnergyStats e : objects) e.setTimestamp(birthday);
		return objects;
	}

	public double[] getPrimitiveSample()
	{
		String energyString = EnergyMonitor.energyStatCheck(0);
		return Utils.stringToPrimitiveArray(energyString);
	}
	
	public double[] getPrimitiveSample(int socket)
	{ 
		String energyString = EnergyMonitor.energyStatCheck(socket);
		return Utils.stringToPrimitiveArray(energyString);
	}	

	public static void main(String[] args) throws InterruptedException
	{
		SyncEnergyMonitor monitor = new SyncEnergyMonitor();
		monitor.init();

		int socket = 1;
		double[] before = monitor.getPrimitiveSample();
		double[] after;
		double[] diff;
		for (int i = 0; i < 1000; i++) {
			try { Thread.sleep(100); }
			catch (Exception e) { e.printStackTrace(); }
			after = monitor.getPrimitiveSample();
			diff = Utils.subtractPrimitiveSamples(after,before);
			System.out.println(Utils.dumpPrimitiveArray(diff));
			before = after;
		}

		monitor.dealloc();
	}

}






















