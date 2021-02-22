
package jRAPL;

//import java.util.Arrays; // sometimes used in main() sample driver

import java.time.Instant;

public class SyncEnergyMonitor extends EnergyMonitor {

	@Override
	public void activate()
	{
		super.activate();
	}

	@Override
	public void deactivate()
	{
		super.deactivate();
	}

	public EnergyStats getSample()
	{
		String energyString = EnergyMonitor.energyStatCheck();
		Instant birthday = Instant.now();
		EnergyStats sample = Utils.stringToEnergyStats(energyString);
		sample.setTimestamp(birthday);
		return sample;
	}

	public double[] getPrimitiveSample()
	{
		String energyString = EnergyMonitor.energyStatCheck();
		return Utils.stringToPrimitiveSample(energyString);
	}

	public static void main(String[] args) throws InterruptedException
	{
		SyncEnergyMonitor monitor = new SyncEnergyMonitor();
		monitor.activate();

		System.out.println(" -- running with primitive array sample...");
		double[] _before = monitor.getPrimitiveSample();
		double[] _after;
		double[] _diff;
		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			_after = monitor.getPrimitiveSample();
			_diff = Utils.subtractPrimitiveSamples(_after,_before);
			System.out.println(Utils.dumpPrimitiveArray(_diff));
			_before = _after;
		}

		System.out.println("\n -- running with EnergyDiff sample...");
		EnergyStats before = monitor.getSample();
		EnergyStats after;
		EnergyDiff diff;
		System.out.println(EnergyDiff.dumpHeader());
		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			after = monitor.getSample();
			diff = EnergyDiff.between(before,after);
			System.out.println(diff.dump());
			before = after;
		}

		System.out.println("\n -- running with EnergyStats sample...");
		System.out.println(EnergyStats.dumpHeader());
		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			System.out.println(monitor.getSample().dump());
		}
		
		EnergyStats stats = monitor.getSample();
		System.out.println("wait 1000ms...");
		Thread.sleep(1000);
		EnergyDiff d = EnergyDiff.between(stats, monitor.getSample());
		System.out.println("EnergyDiff over 1000ms:");
		for (int socket = 1; socket <= ArchSpec.NUM_SOCKETS; socket++) {
			System.out.println("Dram_Sock"+socket+": "+d.getDram(socket));
			System.out.println("Core_Sock"+socket+": "+d.getCore(socket));
			System.out.println("Gpu_Sock"+socket+": "+d.getGpu(socket));
			System.out.println("Package_Sock"+socket+": "+d.getPackage(socket));
		}

		monitor.deactivate();
	}
}
