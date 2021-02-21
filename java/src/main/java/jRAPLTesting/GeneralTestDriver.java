package jRAPLTesting;
import jRAPL.*;

public class GeneralTestDriver
{
	public static void main(String[] args)
	{
		EnergyManager manager = new EnergyManager();
		manager.activate();
		System.out.println(ArchSpec.infoString());
		manager.deactivate();
	}

}
