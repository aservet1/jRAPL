import org.dacapo.harness.CommandLineArgs;   
import org.dacapo.harness.Callback;      
import java.io.File;

import jRAPL.AsyncEnergyMonitor;
import jRAPL.AsyncEnergyMonitorCSide;
import jRAPL.AsyncEnergyMonitorJavaSide;

import java.time.Instant;

public class CalmnessCallback extends Callback {

	public static int currentIter = 0;
	private static final int WARMUPS = Integer.parseInt(System.getProperty("warmups"));

	private String monitorType;
	private AsyncEnergyMonitor energyMonitor;
	private AsyncFrequencyMonitor frequencyMonitor;
	private boolean monitoringEnergy;
	private int samplingRate;

	public CalmnessCallback(CommandLineArgs args) {
		super(args);
		samplingRate = Integer.parseInt(System.getProperty("samplingRate")) ;
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
		frequencyMonitor = new AsyncFrequencyMonitor();
	}

	@Override
	public void start(String benchmark) {
		super.start(benchmark);
		if (monitoringEnergy) {
			energyMonitor.setSamplingRate(samplingRate);
			energyMonitor.start();
		}
		frequencyMonitor.setSamplingRate(500); // just hardcoded for now
		frequencyMonitor.start();
	}

	@Override
	public void stop(long l, boolean w) {
		super.stop(l, w);
		if (monitoringEnergy) energyMonitor.stop();
		frequencyMonitor.stop();
	}

	@Override
	public void complete(String benchmark, boolean valid) {
		super.complete(benchmark, valid);
		currentIter++;
		if (currentIter > WARMUPS) {
			String fileNameBase = String.format(
				"%s/%s_iteration--%d_%s_samplingrate--%d",
				System.getProperty("resultDir"),
				benchmark,
				(currentIter-WARMUPS),
				monitorType != null? monitorType : "nojrapl",
				samplingRate
			);
			if (monitoringEnergy) {
				energyMonitor.writeFileMetadata(null); System.out.printf(" -- monitorType: %s\n",monitorType);

				String mdatfile = fileNameBase + ".metadata.json";
				energyMonitor.writeFileMetadata(mdatfile);
				System.out.println(" ~) wrote to " + mdatfile);

				String datfile = fileNameBase + ".csv";
				energyMonitor.writeFileCSV(datfile);
				System.out.println(" ~) wrote to " + datfile);
			}
			String freqFile = fileNameBase + ".freq.csv";
			frequencyMonitor.writeFileCSV(freqFile);
			System.out.println(" ~) wrote to " + freqFile);
		}
		if (monitoringEnergy) {
			energyMonitor.reset();
		} frequencyMonitor.reset();
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
