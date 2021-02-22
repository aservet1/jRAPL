package jRAPLTesting;

import jRAPL.ArchSpec;
import jRAPL.SyncEnergyMonitor;
import jRAPL.EnergyManager;

//import java.util.Arrays;
import java.time.Instant;
import java.time.Duration;

public class BenchmarkingEnergy
{
	// index corresponds to ArchSpec.[DRAM|CORE|PKG|GPU]_IDX
	// name should store the identifier for each line
	// iters is the number of iterations
	public static class EnergyReadings{
		public double[][] energyStats;
		public Instant times[];

		public EnergyReadings(int iters){
			energyStats = new double[iters][ArchSpec.NUM_STATS_PER_SOCKET*ArchSpec.NUM_SOCKETS];
			times = new Instant[iters];
		}
	}

	public static void printDiffs(EnergyReadings data, String name, int index) {
		// if data != null, prints all the changes in the values from the 
		//   energyStats arrays in data followed by a summary of the totals,
		//   else prints nothing
		if(data == null) return;
		
		Instant timeAtLastNonZero = data.times[0];
		Instant timeAtThisNonZero = null;
		long totalTime = 0;
		long timeDiff = 0;
		double[] before = data.energyStats[0];
		double[] after = null;
		int lastNonZero = 0;
		int totalNonZero = 0;
		double reading = 0;
		double totalEnergy = 0;

		for(int i = 1; i < data.times.length; i++){
			after = data.energyStats[i];
			reading = after[index] - before[index];
			if(reading != 0){
				timeAtThisNonZero = data.times[i];
				timeDiff = Duration.between(timeAtLastNonZero, timeAtThisNonZero).toNanos() / 1000;
				System.out.println(name + " " + reading + " " + timeDiff + " " + lastNonZero);
				totalTime += timeDiff;
				lastNonZero = 0;
				totalNonZero += 1;
				totalEnergy += reading;
				timeAtLastNonZero = timeAtThisNonZero;
				before = after;
			}
			else{
				lastNonZero += 1;
			}
		}
		System.out.println(name + " totals: " + totalEnergy
							+ " " + totalNonZero + " " + totalTime
							+ " " + data.times.length);
	}

	//Runs the SyncMonitor.getPrimitiveSample() function `iter` number of times
	// and return the array of the readings
	public static EnergyReadings getReadings(int iters) {
		SyncEnergyMonitor monitor = new SyncEnergyMonitor();
		monitor.activate();

		EnergyReadings data = new EnergyReadings(iters);
		int i = 0;
		while(i < iters) {
			data.energyStats[i] = monitor.getPrimitiveSample();
			data.times[i] = Instant.now();
			i++;
		}

		monitor.deactivate();
		return data;
	}

	public static void DramGpuCorePackageStats(int iters)
	{
		EnergyReadings data = getReadings(iters);
		if(ArchSpec.DRAM_IDX != -1)	printDiffs(data, "DRAM", ArchSpec.DRAM_IDX);
		if(ArchSpec.GPU_IDX != -1)	printDiffs(data, "GPU", ArchSpec.GPU_IDX);
		if(ArchSpec.CORE_IDX != -1)	printDiffs(data, "CORE", ArchSpec.CORE_IDX);
		if(ArchSpec.PKG_IDX != -1)	printDiffs(data, "PKG", ArchSpec.PKG_IDX);
	}


	private static void usageAbort() {
		System.out.println(
				"\nusage: sudo java jrapltesting.BenchmarkingEnergy <option> <number of iterations>" +
				"\n  option:" +
				"\n    --read-energy-values"
			);
		System.exit(2);
	}

	public static void main(String[] args)
	{
		int iterations;
		if (args.length != 2) {
			usageAbort();
		}
		try {
			iterations = Integer.parseInt(args[1]);
		} catch(NumberFormatException e){
			System.out.println("Illegal value for <number of iterations>");
			usageAbort();
			return;
		}

		EnergyManager manager = new EnergyManager();
		manager.activate();
		
		if(args[0].equals("--read-energy-values")){ //Timing and reading energy register
			DramGpuCorePackageStats(iterations);
		} else {
			manager.deactivate();
			usageAbort();
		}

		manager.deactivate();
	}

}






