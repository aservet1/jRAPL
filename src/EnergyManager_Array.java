
package jrapl;

import java.util.Arrays;

public class EnergyManager_Array extends EnergyManager<double[]>
{

//	public EnergyManager_Array() {}

	/*public void init()
	{
		super.init();
	}*/
	
	public double[] getSample()
	{
		return EnergyCheckUtils.getEnergyStats();
	}
	
	/*public void cleanup()
	{
		super.cleanup();
	}*/

	public static void main(String[] args) throws Exception
	{
		EnergyManager_Array emarr = new EnergyManager_Array();
		emarr.init();
		while (true) {
			double[] a = emarr.getSample();
			System.out.println(Arrays.toString(a));
			Thread.sleep(40);
		} //emarr.cleanup(); -- not reachable
	}


}
