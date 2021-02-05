
package jRAPL;

//import java.util.Arrays; // sometimes used in main() sample driver

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

	// public EnergyStats getSample(int socket)
	// { 
	// 	String energyString = EnergyMonitor.energyStatCheck(socket);
	// 	Instant birthday = Instant.now();
	// 	double[] statsArray = Utils.stringToPrimitiveArray(energyString);
	// 	return new EnergyStats(socket, statsArray, birthday);
	// }

	public EnergyStats getSample()
	{
		String energyString = EnergyMonitor.energyStatCheck(0);
		Instant birthday = Instant.now();
		EnergyStats sample = Utils.stringToEnergyStats(energyString);
		sample.setTimestamp(birthday);
		return sample;
	}

	public double[] getPrimitiveSample()
	{
		String energyString = EnergyMonitor.energyStatCheck(0);
		return Utils.stringToPrimitiveSample(energyString);
	}
	
	// public double[] getPrimitiveSample(int socket)
	// { 
	// 	String energyString = EnergyMonitor.energyStatCheck(socket);
	// 	return Utils.stringToPrimitiveArray(energyString);
	// }	

	public static void main(String[] args) throws InterruptedException
	{
		SyncEnergyMonitor monitor = new SyncEnergyMonitor();
		monitor.init();

		double[] before = monitor.getPrimitiveSample();
		double[] after;
		double[] diff;
		for (int i = 0; i < 10; i++) {
			try { Thread.sleep(100); }
			catch (Exception e) { e.printStackTrace(); }
			after = monitor.getPrimitiveSample();
			diff = Utils.subtractPrimitiveSamples(after,before);
			System.out.println(Utils.dumpPrimitiveArray(diff));
			before = after;
		}

		EnergyStats stats = monitor.getSample();
		Thread.sleep(1000);
		EnergyDiff d = EnergyDiff.between(stats, monitor.getSample());
		System.out.println("EnergyDiff over 1000ms:");
		for (int socket = 1; socket <= ArchSpec.NUM_SOCKETS; socket++) {
			System.out.println("DramSock"+socket+": "+d.atSocket(socket).getDram());
			System.out.println("CoreSock"+socket+": "+d.atSocket(socket).getCore());
			System.out.println("GpuSock"+socket+": "+d.atSocket(socket).getGpu());
			System.out.println("PackageSock"+socket+": "+d.atSocket(socket).getPackage());
		}

		monitor.dealloc();
	}

}






















