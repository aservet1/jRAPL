import org.dacapo.harness.CommandLineArgs;   
import org.dacapo.harness.Callback;      
import java.io.*;

import jRAPL.AsyncEnergyMonitor;
import jRAPL.AsyncEnergyMonitorCSide;
import jRAPL.AsyncEnergyMonitorJavaSide;

import java.time.Instant;

public class AsyncMonitorCallback extends Callback {

	//public static int MAX_ITERATIONS = 20;
	public static int currentIter = 0;
	private static final int WARMUPS = Integer.parseInt(System.getProperty("warmups"));

	private String monitorType;
	private AsyncEnergyMonitor energyMonitor;
	private AsyncMemoryMonitor memoryMonitor;
	private boolean monitoringEnergy;

	public AsyncMonitorCallback(CommandLineArgs args) {
		super(args);
		monitoringEnergy = System.getProperty("monitoringEnergy").equals("true");
		if (monitoringEnergy) {
			monitorType = System.getProperty("monitorType");
			System.out.printf("monitorType = %s\n", monitorType);
			switch (monitorType){
				case "java":
					energyMonitor = new AsyncEnergyMonitorJavaSide();
					break;
				case "c-linklist":
					energyMonitor = new AsyncEnergyMonitorCSide("LINKED_LIST");
					break;
				case "c-dynamicarray":
					energyMonitor = new AsyncEnergyMonitorCSide("DYNAMIC_ARRAY");
					break;
				default:
					System.err.println(String.format("Invalid option for monitorType: '%s'",monitorType));
					System.exit(1);
			}
			energyMonitor.activate();
		}
		memoryMonitor = new AsyncMemoryMonitor();
	}

	@Override
	public void start(String benchmark) {
		super.start(benchmark);
		if (monitoringEnergy) {
			energyMonitor.setSamplingRate(1);
			energyMonitor.start();
		}
		memoryMonitor.setSamplingRate(1); // the idea behind this sampling rate is to track the increase in memory with around every additional sample
		memoryMonitor.start();
		
	}

	@Override
	public void stop(long l, boolean w) {
		super.stop(l, w);
		if (monitoringEnergy) energyMonitor.stop();
		memoryMonitor.stop();
	}

	@Override
	public void complete(String benchmark, boolean valid) {
		super.complete(benchmark, valid);
		currentIter++;
		if (currentIter > WARMUPS) {
			String fileNameBase = String.format(
				"%s/%s_%d_%s",
				System.getProperty("resultDir"),
				benchmark,
				(currentIter-WARMUPS),
				monitorType != null? monitorType : "nojrapl"
			);
			if (monitoringEnergy) {
				energyMonitor.writeFileMetadata(null); System.out.printf(" -- monitorType: %s\n",monitorType);
				energyMonitor.writeFileMetadata(fileNameBase+".metadata.json");
				energyMonitor.writeFileCSV(fileNameBase+".csv");
			}
			memoryMonitor.writeFile(fileNameBase+".memory.json");
		}
		if (monitoringEnergy) {
			energyMonitor.reset();
		} memoryMonitor.reset();
		System.out.println(" .) iteration done at " + Instant.now());
	}
	@Override
	public boolean runAgain() {
		boolean doRun = super.runAgain();
		if (!doRun && monitoringEnergy) {
			energyMonitor.deactivate();
		}
		return doRun;
	}
}
