package jRAPL;

public class EnergyMonitor extends EnergyManager {

	@Override
	public void init() { super.init(); }

	@Override
	public void dealloc() { super.dealloc(); }

	// whichSocket = 0 for all sockets	
	// package private so it can be called in JMH things
	native static String energyStatCheck(int whichSocket);

}













