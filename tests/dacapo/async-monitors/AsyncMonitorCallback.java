
import org.dacapo.harness.CommandLineArgs;   
import org.dacapo.harness.Callback;      
import java.io.*;

import jRAPL.AsyncEnergyMonitor;
import jRAPL.AsyncEnergyMonitorCSide;
import jRAPL.AsyncEnergyMonitorJavaSide;

public class AsyncMonitorCallback extends Callback {

	//public static int MAX_ITERATIONS = 20;
	public static int currentIter = 0;
	private static final int WARMUPS = Integer.parseInt(System.getProperty("warmups"));

	private String monitorType;
	private AsyncEnergyMonitor energyMonitor;
	private AsyncMemoryMonitor memoryMonitor;
	private boolean checkingMemory;
	
	public AsyncMonitorCallback(CommandLineArgs args) {
		super(args);
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
		checkingMemory = System.getProperty("monitoringMemory").equals("true");
		memoryMonitor = checkingMemory ? new AsyncMemoryMonitor() : null ;
	}

	@Override
	public void start(String benchmark) {
		super.start(benchmark);
		energyMonitor.activate();
		energyMonitor.setSamplingRate(1);
		energyMonitor.start();
		if (checkingMemory) memoryMonitor.start();
	}

	@Override
	public void stop(long l, boolean w) {
		super.stop(l, w);
		energyMonitor.stop();
		if (checkingMemory) memoryMonitor.stop();
	}

	@Override
	public void complete(String benchmark, boolean valid) {
		super.complete(benchmark, valid);
		currentIter++;
		if (currentIter > WARMUPS) {
			// System.out.println(m);
			energyMonitor.writeFileMetadata(null);
			String fileNameBase = String.format("%s/%s_%d_%s", System.getProperty("resultDir"), benchmark, (currentIter-WARMUPS), monitorType);
			energyMonitor.writeFileMetadata(fileNameBase+".metadata.json");
			energyMonitor.writeFileCSV(fileNameBase+".csv");
			if (checkingMemory)
				memoryMonitor.writeFile(
					fileNameBase+"_MEMORY"+".json"
				);
		}
		energyMonitor.reset();
		energyMonitor.deactivate();
		if (checkingMemory) memoryMonitor.reset();
	}
}
