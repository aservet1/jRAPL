import jRAPL.SyncEnergyMonitor;
import jRAPL.EnergyStats;
import jRAPL.EnergyDiff;
import java.time.Instant;
import java.time.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;

public class RAPLTiming
{
	static int N = 100000;

	public static void main(String[] args)
	{
		if (args[0].equals("newer")) {
			System.out.println(usingNewer());
		}
		else if (args[0].equals("jRAPL")) {
			System.out.println(usingjRAPL());
		}
		else {
			System.out.println("unknown arg: "+args[0]);
		}
	}

	private static double average(long[] arra) {
	int sum = 0; for (long l : arra) sum += l;
	return sum / arra.length;
	}

	public static double usingNewer() {
		Instant[] insts = new Instant[N];
		for (int i = 0; i < N; ++i) {
			insts[i] = Instant.now();
			usingNewerGetEnergy();
		}
		long[] microtime = new long[N-1];
		for (int i = 1; i < N; ++i) {
			microtime[i-1] = (Duration.between(insts[i-1],insts[i]).toNanos()/1000);
		}
		return average(microtime);
	}
	private static double usingNewerGetEnergy()
	{
		String filePath = "/sys/class/powercap/intel-rapl:0/energy_uj";
		String ujsText = null;
		try {
			ujsText = new String (
				Files.readAllBytes(Paths.get(filePath)),
				StandardCharsets.UTF_8
			).strip();
		} catch (IOException e) {
			e.printStackTrace();
		}
		double js = Long.parseLong(ujsText) / 1000000.0;
		return js;
	}
	
	public static double usingjRAPL()
	{ 
		SyncEnergyMonitor m = new SyncEnergyMonitor();
		m.activate();
		EnergyDiff[] diffs = new EnergyDiff[N];
		EnergyStats after;
		EnergyStats before = m.getSample();
		for (int i = 0; i < N; ++i) {
			after = m.getSample();
			diffs[i] = EnergyDiff.between(before, after);
			before = after;
		}
		long[] microtime = new long[N];
		for (int i = 0; i < diffs.length; ++i) {
			microtime[i] = (diffs[i].getTimeElapsed().toNanos()/1000);
		}
		m.deactivate();
		return average(microtime);
	}
}
