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
		ec.setDelay(10);
		
		new Thread(ec).start();
		try { Thread.sleep(1000); } catch (Exception e) {}
		ec.end();

		System.out.println(ec);
	}

}
