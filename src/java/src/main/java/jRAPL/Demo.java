package jRAPL;

import java.util.Arrays;

public class Demo {

	private static void sleepPrint(int ms) throws InterruptedException {
		int sec = (int)ms/1000;
		ms = ms%1000;
		for (int s = 0; s < sec; s++) {
			System.out.printf("%d/%d\n",s,(sec+ms));
			Thread.sleep(1000);
		} Thread.sleep(ms);
	}

	private static void demoArchSpec() {
		System.out.println(ArchSpec.infoString());
	}

	private static void demoEnergyMonitor() throws InterruptedException {
		EnergyMonitor monitor = new EnergyMonitor();
		monitor.activate();

		System.out.println("\n -- running with EnergyMeasurement sample...");
		EnergySample before = monitor.getEnergySample();
		EnergySample after;
		EnergyMeasurement joules;
		System.out.println(EnergyMeasurement.csvHeader());
		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			after = monitor.getEnergySample();
			joules = EnergyMeasurement.between(before,after);
			System.out.println(joules.csv());
			before = after;
		}

		EnergySample stats = monitor.getEnergySample();
		System.out.println("wait 1000ms...");
		Thread.sleep(1000);
		EnergyMeasurement m = EnergyMeasurement.between(stats, monitor.getEnergySample());
		System.out.println("EnergyMeasurement over 1000ms:");
		for (int socket = 1; socket <= ArchSpec.NUM_SOCKETS; socket++) {
			System.out.println("DRAM_Sock"+socket+": "+m.getDRAM(socket));
			System.out.println("PP0_Sock"+socket+": "+m.getPP0(socket));
			System.out.println("PP1_Sock"+socket+": "+m.getPP1(socket));
			System.out.println("PKG_Sock"+socket+": "+m.getPKG(socket));
		}

		monitor.deactivate();
	}

	public static void demoAsyncEnergyMonitor() throws InterruptedException {
		AsyncEnergyMonitor m = new AsyncEnergyMonitor();

		m.activate();
		m.setSamplingRate(100);

		m.start();
        sleepPrint(3000);
		m.stop();

		System.out.println(m);
		int k = 5;
		System.out.println(Arrays.toString(m.getLastKMeasurements(k)));

		m.writeFileMetadata("AsyncMonitor-metainfo.json");
		m.writeFileCSV("AsyncMonitor.csv");

        m.reset();
		m.deactivate();
	}

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 0) {
			System.out.println(" .) Must provide run option args: SyncEnergyMonitor, AsyncEnergyMonitor, or ArchSpec");
			System.exit(args.length);
		}
		switch (args[0]) {
			case "EnergyMonitor" :      demoEnergyMonitor();  break;
			case "AsyncEnergyMonitor" : demoAsyncEnergyMonitor(); break;
			case "ArchSpec" :           demoArchSpec();           break;
			default :   System.out.println("invalid args: " + Arrays.toString(args));
		}
	}
}

