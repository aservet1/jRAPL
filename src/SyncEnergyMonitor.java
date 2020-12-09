
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

	// returns a[i] - b[i] for i in range(len(a)), where len(a) == len(b)
	// @TODO consider whether this class is the best place to hold this function
	// This method would probably be ok living in here, but consider outsourcing
	// if you can find something more appropriate for where this guy will live
	public static double[] subtractPrimitiveSamples(double[] a, double[] b)
	{
		assert ( a.length == b.length );
		double[] diff = new double[a.length];
		for (int i = 0; i < diff.length; i++) {
			diff[i] = a[i] - b[i];
			if (diff[i] < 0)
				diff[i] += ArchSpec.RAPL_WRAPAROUND;
		}

		return diff;
	}

	// @TODO consider whether this class is the best place to hold this function
	// This method would probably be ok living in here, but consider outsourcing
	// if you can find something more appropriate for where this guy will live,
	// a more contrally accessible location maybe
	//
	// returns string-joined version of a: a[1],a[2],...,a[n]
	public static String dumpPrimitiveArray(double[] a)
	{
		String s = new String();
		int i; for (i = 0; i < a.length-1; i++) {
			s += String.format("%4f",a[i]) + ",";
		} s += String.format("%4f",a[i]);
		return s;
	}

	public static void main(String[] args) throws InterruptedException
	{
		SyncEnergyMonitor monitor = new SyncEnergyMonitor();
		monitor.init();

		int socket = 1;
		//double[] before = monitor.getPrimitiveSample();
		//double[] after;
		//double[] diff;
		for (int i = 0; i < 1000; i++) {
			try { Thread.sleep(57); }
			catch (Exception e) { e.printStackTrace(); }
			//after = monitor.getPrimitiveSample();
			//diff = SyncEnergyMonitor.subtractPrimitiveSamples(after,before);
			//System.out.println(dumpPrimitiveArray(diff));
			//before = after;
			System.out.println(dumpPrimitiveArray(monitor.getPrimitiveSample()));
		}

		monitor.dealloc();
	}

}






















