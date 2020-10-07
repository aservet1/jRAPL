
package jrapl;

public abstract class AsyncEnergyManager {

	//protected AsyncEnergyMonitor[] monitors;
	
	public void init()
	{
		JRAPL.ProfileInit();
	}

	public void cleanup()
	{
		JRAPL.ProfileDealloc();
	}


	//  something about whether to return individual guys or have a whole
	//  collection of these guys circling around in here and start/stopping
	//  whatever many multi threaded operations are going on in here...(should learn more about in what ways one can do multi threading)
	public abstract AsyncEnergyMonitor getMonitor(int samplingRate);

}
