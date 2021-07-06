
import jRAPL.AsyncEnergyMonitor;
import jRAPL.AsyncEnergyMonitorCSide;
import jRAPL.AsyncEnergyMonitorJavaSide;

public class Driver {

	private static void sleepPrint(int seconds, String label) throws InterruptedException {
		System.out.printf("Sleeping for %d seconds\n",seconds);
		for (int s = 0; s < seconds; s++) {
			Thread.sleep(1000);
			System.out.printf("%d\t",s+1);
		} System.out.printf("\n");
	}

	public static void main(String[] args) throws InterruptedException {

		int samplingRate = 1;
		AsyncEnergyMonitor energyMonitor = null;
		if (args[0].equals("Java")) {
			energyMonitor = new AsyncEnergyMonitorJavaSide();
			energyMonitor.setSamplingRate(samplingRate);
		} else if (args[0].equals("C")) {
			energyMonitor = new AsyncEnergyMonitorCSide(samplingRate,args[1],1024);
		} else System.out.printf("invalid args[0]: %s\n", args[0]);
		energyMonitor.activate();

		AsyncMemoryMonitor memoryMonitor = new AsyncMemoryMonitor();
		memoryMonitor.setSamplingRate(samplingRate);

		memoryMonitor.start(); energyMonitor.start();
		sleepPrint(500,args[0]);
		energyMonitor.stop(); memoryMonitor.stop();

		String name = args[0]+"_"+(args[0].equals("Java")?"":args[1]+"_");
		memoryMonitor.writeFile(name+"memory_result.json");
		energyMonitor.writeFileCSV(name+"energy_result.csv");
	}

}
