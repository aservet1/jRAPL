
package jrapl;

import java.util.Arrays;

public class SyncEnergyMonitor extends EnergyMonitor {

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public void dealloc()
	{
		super.dealloc();
	}

	public EnergyStats getObjectSample(int socket) // for a specific socket
	{
		return EnergyStats.get()[socket-1];
	}

	public EnergyStats[] getObjectSample()
	{
		return EnergyStats.get();
	}

	public double[] getPrimitiveSample(int socket)
	{
		int lo = socket-1;
		int hi = lo + ArchSpec.NUM_SOCKETS;
		return Arrays.copyOfRange(EnergyCheckUtils.getEnergyStats(),lo,hi);
	}

	public double[] getPrimitiveSample()
	{
		return EnergyCheckUtils.getEnergyStats();
	}

	public static void main(String[] args)
	{
		SyncEnergyMonitor monitor = new SyncEnergyMonitor();
		monitor.init();
		System.out.println(monitor.getObjectSample(1));
		System.out.println(Arrays.toString(monitor.getObjectSample()));
		monitor.dealloc();
	}

}
