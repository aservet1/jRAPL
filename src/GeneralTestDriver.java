package jrapl;

import java.util.ArrayList;

public class GeneralTestDriver
{
	public static void main(String[] args)
	{
		/*ArrayList<Integer> al = new ArrayList<Integer>();
		for (int x = 0; x < 97; x++) al.add(x);
		System.out.println(al.size());*/
		EnergyReadingCollector ec = new EnergyReadingCollector();
		ec.setDelay(25);
		Thread t1 = new Thread(ec);
		t1.start();
		try { Thread.sleep(1000); } catch (Exception e) {}
		ec.end();

		ArrayList<double[]> readings = ec.getReadings();
		for (int i = 0; i < readings.size(); i++) {
			System.out.println("dram:\t"+readings.get(i)[0]+"\tcpu:\t"+readings.get(i)[1]+"\tpkg:\t"+readings.get(i)[2]);
		}
		System.out.println(ec);
	}

}
