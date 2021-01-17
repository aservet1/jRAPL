import jRAPL.EnergyManager;
public class StressTest
{
	// EnergyManager m;
	// static{
	// 	m = new EnergyManager();
	// }
	public static void main(String[] args) throws InterruptedException
	{
		new EnergyManager().init();
		for (int i = 0; i < 2000000; i++) {
			EnergyManager.profileInit();
			int x = 0;
			while (x < 1000000) {
				x++;
			}
			System.out.println(x);
		}
	}
}
