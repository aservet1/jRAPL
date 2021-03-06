
import org.dacapo.harness.CommandLineArgs;   
import org.dacapo.harness.Callback;      
import java.io.*;

import jRAPL.AsyncEnergyMonitor;
import jRAPL.AsyncEnergyMonitorCSide;
import jRAPL.AsyncEnergyMonitorJavaSide;

public class AsyncMonitorCallback extends Callback {

	//public static int MAX_ITERATIONS = 20;
	public static int currentIter = 0;
	private static final int WARMUPS = 5;

	private String monitorType;
	AsyncEnergyMonitor m;
	
	public AsyncMonitorCallback(CommandLineArgs args) {
		super(args);
		monitorType = System.getProperty("monitorType");
		System.out.printf("monitorType = %s\n", monitorType);
		switch (monitorType){
			case "java":
				m = new AsyncEnergyMonitorJavaSide();
				break;
			case "c-linklist":
				m = new AsyncEnergyMonitorCSide("LINKED_LIST");
				break;
			case "c-dynamicarray":
				m = new AsyncEnergyMonitorCSide("DYNAMIC_ARRAY");
				break;
			default:
				System.err.println(String.format("Invalid option for monitorType: '%s'",monitorType));
				System.exit(1);
		}
	}

	@Override
	public void start(String benchmark) {
		super.start(benchmark);
		m.activate();
		m.setSamplingRate(0);
		m.start();
	}

	@Override
	public void stop(long l, boolean w) {
		super.stop(l, w);
		m.stop();
	}

	@Override
	public void complete(String benchmark, boolean valid) {
		super.complete(benchmark, valid);
		currentIter++;
		if (currentIter > WARMUPS) {
			System.out.println(m);
			String fileNameBase = String.format("%s/%s_%d_%s", System.getProperty("resultDir"), benchmark, (currentIter-WARMUPS), monitorType);
			m.writeFileMetadata(fileNameBase+".metadata.json");
			m.writeFileCSV(fileNameBase+".csv");
		}
		m.reset();
		m.deactivate();
	}

}
