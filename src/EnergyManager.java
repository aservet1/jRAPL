
package jrapl;


public abstract class EnergyManager<T>
{

	public void init() //get a better name
	{
		JRAPL.ProfileInit();
	}

	public abstract T getSample();

	public void cleanup()
	{
		JRAPL.ProfileDealloc();
	}

}

