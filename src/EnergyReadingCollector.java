package jrapl;

import java.util.ArrayList;

public class EnergyReadingCollector implements Runnable
{
	private ArrayList<double[]> readings;
	private int delay; // milliseconds
	static volatile boolean exit = false;

	public EnergyReadingCollector()
	{
		delay = 10;
		readings = new ArrayList<double[]>();
	}

	public EnergyReadingCollector(int d)
	{
		delay = d;
		readings = new ArrayList<double[]>();
	}

	public double[][] getReadings()
	{
		double[][] readings_array = new double[readings.size()][];
		for (int i = 0; i < readings.size(); i++)
			readings_array[i] = readings.get(i);
		return readings_array;
	}

	public int getDelay()
	{
		return delay;
	}

	public void setDelay(int d)
	{
		delay = d;
	}

	public double[] readOverDelay()
	{
		double[] before = EnergyCheckUtils.getEnergyStats();
		try { Thread.sleep(delay); } catch (Exception e) {}
		double[] after  = EnergyCheckUtils.getEnergyStats();
		double[] reading = new double[3];
		for (int i = 0; i < reading.length; i++){
			reading[i] = after[i]-before[i];
		}
		return reading;
	}

	//deletes previous reading list record, make sure you save it if you need it before running thread
	public void run()
	{
		exit = false; readings = new ArrayList<double[]>();
		while (!exit)
		{
			double[] reading = readOverDelay();
			try { Thread.sleep(delay);  } catch (Exception e) {}
			readings.add(reading);
		}
	}

	public void end()
	{
		exit = true;
	}

	public String toString()
	{
		String s = "";
		s += "readings: " + readings.size() + "\n";
		s += "delay: " + delay + "\n";
		if (readings.size() != 0) {
			int i;
			for (i = 0; i < readings.size()-1; i++) {
					double[] reading = readings.get(i);
					s += (i+1) + "\tdram:\t" + reading[0] + "\tcore:\t" + reading[1] + "\tpkg:\t" + reading[2] + "\n";
				}
			double[] reading = readings.get(i);
			s += (i+1) + "\tdram:\t" + reading[0] + "\tcore:\t" + reading[1] + "\tpkg:\t" + reading[2];
		}
		return s;
	}

	/*public static void main(String[] args)
	{
		EnergyReadingCollector ec = new EnergyReadingCollector(10);

		new Thread(ec).start();
		try { Thread.sleep(500); } catch (Exception e) {}
		ec.end();
		System.out.println(ec);

		new Thread(ec).start();
		try { Thread.sleep(250); } catch (Exception e) {}
		ec.end();
		System.out.println(ec);

	}*/

}
