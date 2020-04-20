package kenan;

import org.dacapo.harness.Callback;      
import org.dacapo.harness.CommandLineArgs;   
import java.io.*;
import java.lang.reflect.*;

public class IterationCallBack extends Callback {


public static  int MAX_ITERATIONS = 20;
public static  int CURRENT_ITERATION = 1;
public static  long[] START_ITER_TS = new long[MAX_ITERATIONS];
public static  long[] STOP_ITER_TS = new long[MAX_ITERATIONS];
private static final int FIRE_AFTER = 5;

public void stop(boolean w) {
	super.stop(w);
	STOP_ITER_TS[CURRENT_ITERATION-1] = System.currentTimeMillis();
	CURRENT_ITERATION++;
	System.out.println("Iteration Stopping");
	//if(CURRENT_ITERATION==5) {
		try {
			Class cls = Class.forName("org.jikesrvm.VM");
			System.out.println("Calling end iteration");
			Method m = cls.getDeclaredMethod("end_iteration", null);
			Object o = m.invoke(null, null);
		} catch(Exception exc) {
			 exc.printStackTrace();
		}
	//}
	
}


public void start(String benchmark) {
	super.start(benchmark);
	START_ITER_TS[CURRENT_ITERATION-1] = System.currentTimeMillis();
	

}

public IterationCallBack(CommandLineArgs args) {
	super(args);
}

public void complete(String benchmark, boolean valid) {

	try {
		FileWriter fileWriter = new FileWriter("iteration_times");
		PrintWriter printWriter = new PrintWriter(fileWriter);
		for(int i=1; i<= CURRENT_ITERATION;i++) {
			printWriter.printf("%d,%d \n",START_ITER_TS[i-1],STOP_ITER_TS[i-1]);
		}
		printWriter.close();
		
	} catch(Exception exception) {
		System.out.println(exception.getMessage());
	}
}

}

