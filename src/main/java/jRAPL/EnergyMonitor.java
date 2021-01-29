package jRAPL;

public class EnergyMonitor extends EnergyManager {

	public void init() {
		super.init();
	}

	public void dealloc() {
		super.dealloc();
	}

	// whichSocket = 0 for all sockets	
	// package private so it can be called in JMH things
	native static String energyStatCheck(int whichSocket);

}













