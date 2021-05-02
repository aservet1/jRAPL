import com.javamex.classmexer.MemoryUtil;
import jRAPL.*;
import java.util.Arrays;

public class ObjectMemory
{
	static void rawSampleFootprint(SyncEnergyMonitor m) {
		double[] primitiveSample = m.getPrimitiveSample();
		EnergyStats objectSample = m.getSample();

		long primitiveSampleBytes = MemoryUtil.deepMemoryUsageOf(primitiveSample);
		long objectSampleBytes = MemoryUtil.deepMemoryUsageOf(objectSample);
		System.out.println("  primitive sample: " + primitiveSampleBytes + " bytes");
		System.out.println("  object sample: " + objectSampleBytes + " bytes");

		System.out.println("    "+Arrays.toString(primitiveSample));
		System.out.println("    "+objectSample.csv());
	}
	
	static void diffSampleFootprint(SyncEnergyMonitor m) throws InterruptedException {
		double[] beforeArr = m.getPrimitiveSample();
		Thread.sleep(100);
		double[] afterArr  = m.getPrimitiveSample();
		double[] primitiveSample = new double[beforeArr.length];
		for (int i = 0; i < beforeArr.length; i++)
			primitiveSample[i] = afterArr[i] - beforeArr[i];

		EnergyStats before = m.getSample();
		Thread.sleep(100);
		EnergyStats after = m.getSample();
		EnergyDiff objectSample = EnergyDiff.between(before, after);

		long primitiveSampleBytes = MemoryUtil.deepMemoryUsageOf(primitiveSample);
		long objectSampleBytes = MemoryUtil.deepMemoryUsageOf(objectSample);
		System.out.println("  primitive sample: " + primitiveSampleBytes + " bytes");
		System.out.println("  object sample: " + objectSampleBytes + " bytes");

		System.out.println("    "+Arrays.toString(primitiveSample));
		System.out.println("    "+objectSample.csv());
	}


	public static void main(String[] args) throws InterruptedException
	{
		SyncEnergyMonitor m = new SyncEnergyMonitor();
		m.activate();
		System.out.println("< ----------");
		System.out.println("For raw stamp sample:");
		rawSampleFootprint(m);
		System.out.println("");
		System.out.println("For diff sample (over a 100ms delay):");
		diffSampleFootprint(m);
		System.out.println("---------- >");
		m.deactivate();
	}
}
