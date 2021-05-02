// import java.io.*;
// 
// import jRAPL.AsyncEnergyMonitor;
// import jRAPL.AsyncEnergyMonitorCSide;
// import jRAPL.AsyncEnergyMonitorJavaSide;
// 
// public class NoDacapoCallback {
// 
// 	//public static int MAX_ITERATIONS = 20;
// 	public static int currentIter = 0;
// 	private static final int WARMUPS = Integer.parseInt(System.getProperty("warmups"));
// 
// 	private String monitorType;
// 	private AsyncEnergyMonitor energyMonitor;
// 	private AsyncMemoryMonitor memoryMonitor;
// 	private boolean monitoringEnergy;
// 
// 	public NoDacapoCallback() {
// 		monitoringEnergy = System.getProperty("monitoringEnergy").equals("true");
// 
// 		if (monitoringEnergy) {
// 			monitorType = System.getProperty("monitorType");
// 			System.out.printf("monitorType = %s\n", monitorType);
// 			switch (monitorType){
// 				case "java":
// 					energyMonitor = new AsyncEnergyMonitorJavaSide();
// 					break;
// 				case "c-linklist":
// 					energyMonitor = new AsyncEnergyMonitorCSide("LINKED_LIST");
// 					break;
// 				case "c-dynamicarray":
// 					energyMonitor = new AsyncEnergyMonitorCSide("DYNAMIC_ARRAY");
// 					break;
// 				default:
// 					System.err.println(String.format("Invalid option for monitorType: '%s'",monitorType));
// 					System.exit(1);
// 			}
// 		}
// 		memoryMonitor = new AsyncMemoryMonitor();
// 	}
// 
// 	public void start() {
// 		if (monitoringEnergy) {
// 			energyMonitor.activate();
// 			energyMonitor.setSamplingRate(1);
// 			energyMonitor.start();
// 		}
// 		memoryMonitor.setSamplingRate(1); // the idea behind this sampling rate is to track the increase in memory with around every additional sample
// 		memoryMonitor.start();
// 	}
// 
// 	public void stop() {
// 		if (monitoringEnergy) energyMonitor.stop();
// 		memoryMonitor.stop();
// 	}
// 
// 	public void complete() {
// 		currentIter++;
// 		if (currentIter > WARMUPS) {
// 			String fileNameBase = String.format("%s/%s_%d_%s", System.getProperty("resultDir"), "NoDacapo", (currentIter-WARMUPS), monitorType != null? monitorType : "nojrapl");
// 			
// 			if (monitoringEnergy) {
// 				energyMonitor.writeFileMetadata(null); System.out.printf(" -- monitorType: %s\n",monitorType);
// 				energyMonitor.writeFileMetadata(fileNameBase+".metadata.json");
// 				energyMonitor.writeFileCSV(fileNameBase+".csv");
// 			}
// 			memoryMonitor.writeFile(fileNameBase+".memory.json");
// 		}
// 		if (monitoringEnergy) {
// 			energyMonitor.reset();
// 			energyMonitor.deactivate();
// 		}
// 		memoryMonitor.reset();
// 	}
// 
// 	public static void main(String[] args) {
// 		if (args.length != 2) {
// 			System.err.println("command line args: num_iterations");
// 			System.exit(1);
// 		}
// 
// 		for (int i = 0; i < Integer.parseInt(args[0]); i++) {
// 			NoDacapoCallback cb = NoDacapoCallback();
// 			cb.start();
// 			try {
// 				Thread.sleep(120000);
// 			} catch (InterrputedException e) {
// 				System.err.println("interrupted exception!");
// 				System.exit(1);
// 			}
// 			cb.stop();
// 			cb.complete();
// 		}
// 	}
// }
