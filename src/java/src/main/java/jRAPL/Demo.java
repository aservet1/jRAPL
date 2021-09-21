package jRAPL;

import java.util.Arrays;
import java.time.Instant;
import java.time.Duration;

public class Demo {

	private static class DemoUtils {

		public static void sleepPrint(int ms) throws InterruptedException {
			int sec = (int)ms/1000;
			ms = ms%1000;
			for (int s = 0; s < sec; s++) {
				System.out.printf("%d/%d\n",s,(sec+ms));
				Thread.sleep(1000);
			} Thread.sleep(ms);
		}

	    public static String csvPrimitiveArray(double[] a) {
			String s = new String();
			int i; for (i = 0; i < ArchSpec.NUM_SOCKETS*ArchSpec.NUM_STATS_PER_SOCKET-1; i++) {
				s += String.format("%.6f",a[i]) + ",";
			} s += String.format("%.6f",a[i]);
			return s;
		}
	
		public static String csvPrimitiveArray(double[] a, Instant timestamp) {
			return String.format("%s,%d", csvPrimitiveArray(a), Utils.timestampToUsec(timestamp));
		}
	
		public static String csvPrimitiveArray(double[] a, Duration elapsedTime) {
			return String.format("%s,%d", csvPrimitiveArray(a), Utils.durationToUsec(elapsedTime));
		}

	}

	private static void demoArchSpec() {
		System.out.println(ArchSpec.infoString());
	}

	private static void demoSyncEnergyMonitor() throws InterruptedException {
		SyncEnergyMonitor monitor = new SyncEnergyMonitor();
		monitor.activate();

		System.out.println(" -- running with primitive array sample...");
		double[] _before = monitor.getPrimitiveSample();
		double[] _after;
		double[] _diff;
		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			_after = monitor.getPrimitiveSample();
			_diff = EnergyMonitor.subtractPrimitiveSamples(_after,_before);
			System.out.println(DemoUtils.csvPrimitiveArray(_diff));
			_before = _after;
		}

		System.out.println("\n -- running with EnergyDiff sample...");
		EnergyStats before = monitor.getSample();
		EnergyStats after;
		EnergyDiff diff;
		System.out.println(EnergyDiff.csvHeader());
		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			after = monitor.getSample();
			diff = EnergyDiff.between(before,after);
			System.out.println(diff.csv());
			before = after;
		}

		System.out.println("\n -- running with EnergyStats sample...");
		System.out.println(EnergyStats.csvHeader());
		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			System.out.println(monitor.getSample().csv());
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

	public static void demoAsyncEnergyMonitor(String[] args) throws InterruptedException {
		AsyncEnergyMonitor m = null;
		if (args[1].equalsIgnoreCase("Java")) {
			m = new AsyncEnergyMonitorJavaSide();
		} else if (args[1].equalsIgnoreCase("C")) {
			m = new AsyncEnergyMonitorCSide(args[2]);
		} else {
			System.out.println("invalid args[1]: "+args[1]); //@TODO more robust error message
			System.exit(2);
		}
		m.activate();
		m.setSamplingRate(100);

		m.start();
		DemoUtils.sleepPrint(3000);
		m.stop();

		System.out.println(m);
		int k = 5;
		System.out.println(Arrays.deepToString(m.getLastKSamples_Arrays(k)));
		System.out.println();
		System.out.println(Arrays.toString(m.getLastKSamples(m.getNumSamples())));

		String name = args[1] + ((args.length == 2) ? "" : args[2]);

		m.writeFileMetadata("AsyncMonitor-"+name+"-metainfo.json");
		m.writeFileCSV("AsyncMonitor-"+name+".csv");

		m.reset();
		m.deactivate();
	}

	public static void main(String[] args) throws InterruptedException {

		if (args.length == 0) {
			System.out.println(" .) Must provide run option args: SyncEnergyMonitor, AsyncEnergyMonitor, or ArchSpec");
			System.exit(args.length);
		}
	
		switch (args[0]) {
			case "SyncEnergyMonitor":
				demoSyncEnergyMonitor();
				break;
			case "AsyncEnergyMonitor":
				demoAsyncEnergyMonitor(args);
				break;
			case "ArchSpec":
				demoArchSpec();
				break;
			default:
				System.out.println("invalid args: " + Arrays.toString(args));
		}
	
	}


}
