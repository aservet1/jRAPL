
import org.dacapo.harness.CommandLineArgs;   
import org.dacapo.harness.Callback;      
import java.io.*;

import jRAPL.AsyncEnergyMonitor;
import jRAPL.AsyncEnergyMonitorCSide;
import jRAPL.AsyncEnergyMonitorJavaSide;

public class AsyncMonitorCallback extends Callback {

	//public static int MAX_ITERATIONS = 20;
	public static int currentIter = 0;
	private static final int WARMUPS = 3;

	private String monitorType;
	AsyncEnergyMonitor m;
	
	public AsyncMonitorCallback(CommandLineArgs args) {
		super(args);
		monitorType = System.getProperty("monitorType");
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
		currentIter++;
		
		if (currentIter > WARMUPS) {
			System.out.println(m);
			m.writeToFile(String.format("%d-%s.log", currentIter, monitorType));
		}
		m.reset();
	}

	@Override
	public void complete(String benchmark, boolean valid) {
		super.complete(benchmark, valid);
		m.deactivate();
	}

}
