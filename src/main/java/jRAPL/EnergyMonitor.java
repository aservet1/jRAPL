package jRAPL;

public class EnergyMonitor extends EnergyManager {

	public void init() {
		super.init();
	}

	public void dealloc() {
		super.dealloc();
	}

	// whichSocket = 0 for all sockets	
	public native static String energyStatCheck(int whichSocket);

}













