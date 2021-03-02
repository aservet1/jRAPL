
import org.dacapo.harness.Callback;      
import org.dacapo.harness.CommandLineArgs;   
import java.io.*;

import jRAPL.AsyncEnergyMonitor;
import jRAPL.AsyncEnergyMonitorCSide;
import jRAPL.AsyncEnergyMonitorJavaSide;

public class AsyncMonitorCallback extends Callback {

	public static int MAX_ITERATIONS = 20;
	public static int CURRENT_ITERATION = 1;
	
	private static final int FIRE_AFTER = 2;

	AsyncEnergyMonitor m;
	
	public AsyncMonitorCallback(CommandLineArgs args) {
		super(args);
		m = new AsyncEnergyMonitorCSide();
	}

	@Override
	public void stop(long l, boolean w) {
		super.stop(l, w);
		m.stop();	
		
		CURRENT_ITERATION++;
		
		if (CURRENT_ITERATION > FIRE_AFTER) {
			System.out.println(m);
			m.writeToFile(CURRENT_ITERATION+".log");
		}
		
		m.reset();
	}

	@Override
	public void start(String benchmark) {
		super.start(benchmark);
		m.activate();
		m.setSamplingRate(0);
		m.start();
	}

	@Override
	public void complete(String benchmark, boolean valid) {
		super.complete(benchmark, valid);
		// try {
		// 	FileWriter fileWriter = new FileWriter("iteration_times");
		// 	PrintWriter printWriter = new PrintWriter(fileWriter);
		// 	for(int i = FIRE_AFTER; i <= CURRENT_ITERATION; i++) {
		// 		printWriter.printf("%d\n",STOP_ITER_TS[i],START_ITER_TS[i]);
		// 	}
		// 	printWriter.close();
		// 	
		// } catch(Exception exception) {
		// 	System.out.println(exception.getMessage());
		// }
		m.deactivate();
	}

}
