
import harness.org.dacapo.harness.Callback;      
import harness.org.dacapo.harness.CommandLineArgs;   
import java.io.*;

import jRAPL.AsyncEnergyMonitor;
import jRAPL.AsyncEnergyMonitorJavaSide;

public class MyCustomCallback extends Callback {

	public static int MAX_ITERATIONS = 20;
	public static int CURRENT_ITERATION = 1;
	
	public static long[] START_ITER_TS = new long[MAX_ITERATIONS];
	public static long[] STOP_ITER_TS = new long[MAX_ITERATIONS];
	
	private static final int FIRE_AFTER = 5;

	AsyncEnergyMonitor m;
	
	public MyCustomCallback(CommandLineArgs args) {
		super(args);
		System.out.println("~~ "+CommandLineArgs+" ~~");
		m = new AsyncEnergyMonitorJavaSide();
		m.activate();
	}

	@Override
	public void stop(boolean w) {
		super.stop(w);
		m.stop();	

		STOP_ITER_TS[CURRENT_ITERATION-1] = System.currentTimeMillis();
		CURRENT_ITERATION++;
	
		System.out.println("Iteration "+CURRENT_ITERATION+" Stopping");
		
		if (CURRENT_ITERATION > FIRE_AFTER) {
			System.out.println(m);
		}
		
		m.reset();
	}

	@Override
	public void start(String benchmark) {
		super.start(benchmark);
		m.start();
		START_ITER_TS[CURRENT_ITERATION-1] = System.currentTimeMillis();
	}

	@Override
	public void complete(String benchmark, boolean valid) {
		try {
			FileWriter fileWriter = new FileWriter("iteration_times");
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for(int i = FIRE_AFTER; i <= CURRENT_ITERATION; i++) {
				printWriter.printf("%d,%d \n",START_ITER_TS[i],STOP_ITER_TS[i]);
			}
			printWriter.close();
			
		} catch(Exception exception) {
			System.out.println(exception.getMessage());
		}
		m.deactivate();
	}

}
