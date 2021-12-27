import jRAPL.*;
import java.io.*;
public class MeasureEnergy
{
	public static void main(String[] args)
	{
		SyncEnergyMonitor m = new SyncEnergyMonitor();
		m.activate();
		EnergyStats before = m.getSample();

		execCmd(args);

		EnergyStats after = m.getSample();
		EnergyDiff consumed = EnergyDiff.between(before, after);
		m.deactivate();
		String[] names = EnergyDiff.csvHeader().split(",");
		String[] values = consumed.csv().split(",");
		for (int i = 0; i < names.length; i++)
			System.out.printf("%s:\t%s\n",names[i],values[i]);
	}

	private static void execCmd(String... args) {
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.directory(new File("."));
		try {
			Process p = pb.start();
			p.waitFor();
		} catch (IOException ioex) {
			System.out.println(" ~) io exception");
			ioex.printStackTrace();
		} catch (InterruptedException intex) {
			System.out.println(" ~) interrupted exception");
			intex.printStackTrace();
		}
	}

}
