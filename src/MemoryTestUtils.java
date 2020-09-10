package jrapltesting;

import java.util.ArrayList;
import java.util.Arrays;
import jrapl.*;

public class MemoryTestUtils {

	public static void main(String[] args) {
		if (args.length == 0) usage_abort();

		String representation = args[0];
		long millisecondDelay = Long.parseLong(args[1]);
		memoryThing(representation, millisecondDelay);
	}

	private static void usage_abort() {
		System.out.println(
				"usage: sudo java jrapltesting.MemoryTestUtils <Object|Array> <m>" +
				"\n\tObject | Array -- Pick one, testing memory footprint of" +
				"\n\t\tAsyncEnergyMonitor with EnergyStats object" +
				"\n\t\t  or EnergyCheckUtils.getEnergyStats() implemented" +
				"\n\t\t  as the main memory storage unit." +
				"\n\tm -- milliseconds to stall the program and keep the thread alive."
			);
		System.exit(2);
	}

	private static void memoryThing(String representation, long millisecondDelay) {
		AsyncMemoryMonitor memmon = new AsyncMemoryMonitor();
		memmon.start(); {
			AsyncMonitor emonn = null;
			if (representation.equals("Object")) emonn = new AsyncEnergyMonitorJavaSide_ObjectSamples();
			if (representation.equals("Array"))  emonn = new AsyncEnergyMonitorJavaSide_ArraySamples();
		
			emonn.start();
			try{ Thread.sleep(millisecondDelay); } catch (Exception e) {}
			emonn.stop();
		} memmon.stop();

		System.out.println(memmon);
	}

}
