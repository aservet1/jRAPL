package jRAPL;

public class EnergyMonitor extends EnergyManager {

	@Override
	public void activate() { super.activate(); }

	@Override
	public void deactivate() { super.deactivate(); }

	// whichSocket = 0 for all sockets	
	// package private so it can be called in JMH things
	native static String energyStatCheck();
}
