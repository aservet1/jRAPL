
package jrapl;

public class EnergyManager_EStatObj extends EnergyManager<EnergyStats[]>
{

//	public EnergyManager_Array() {}

	/*public void init()
	{
		super.init();
	}*/
	
	public EnergyStats[] getSample()
	{
		return EnergyStats.get();
	}
	
	/*public void cleanup()
	{
		super.cleanup();
	}*/

	public static void main(String[] args) throws Exception
	{
		EnergyManager_EStatObj emobj = new EnergyManager_EStatObj();
		emobj.init();
		while (true) {
			EnergyStats[] o = emobj.getSample();
			for( int i = 0; i < o.length; i++ )
				System.out.println(o[i]);
			Thread.sleep(40);
		} //emobj.cleanup(); -- not reachable
	}


}
