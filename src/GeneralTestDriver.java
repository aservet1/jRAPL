package jrapltesting;

import java.util.ArrayList;
import java.util.Arrays;
import jrapl.*;

public class GeneralTestDriver
{
	public static void main(String[] args)
	{
		threadThing(args);
		//memoryThing("Object");
		//memoryThing("Array");
	}

	private static void memoryThing(String representation)
	{
		AsyncMemoryMonitor memmon = new AsyncMemoryMonitor();
		if (representation.equals("Object")) {
			AsyncEnergyMonitorJavaSide_ObjectSamples emonn = new AsyncEnergyMonitorJavaSide_ObjectSamples();
			memmon.start();
			emonn.start();
			try{ Thread.sleep(5000); } catch (Exception e) {}
			emonn.stop();
			memmon.stop();
			System.out.println(memmon);
		} else if (representation.equals("Array")) {
			AsyncEnergyMonitorJavaSide_ArraySamples emonn = new AsyncEnergyMonitorJavaSide_ArraySamples();
			memmon.start();
			emonn.start();
			try{ Thread.sleep(5000); } catch (Exception e) {}
			emonn.stop();
			memmon.stop();
			System.out.println(memmon);
		} else { 
			System.out.println("invalid representation choice: " + representation);
			System.exit(1);
		}

	}

	private static void threadThing(String[] args)
	{
		JRAPL.ProfileInit();

		AsyncEnergyMonitorJavaSide_ArraySamples emonn = new AsyncEnergyMonitorJavaSide_ArraySamples(100);

		emonn.start();
		try { Thread.sleep(5000); } catch (Exception e) {}
		emonn.stop();

		System.out.println("hello w0rld");
		if (args.length == 0) System.out.println(emonn);
		else emonn.writeToFile(args[0]);

		JRAPL.ProfileDealloc();
	}

}
