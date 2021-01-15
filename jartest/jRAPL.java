package pkg;
import jrapl.SyncEnergyMonitor;

public class jRAPL
{
	public static void main(String[] args)
	{
		SyncEnergyMonitor m = new SyncEnergyMonitor();
		m.init();
		System.out.println(m.getObjectSample(1).dump());
		m.dealloc();
	}
}
