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

	public ArrayList<double[]> getReadings()
	{
		return new ArrayList<double[]>(this.readings);
	}

	public int getDelay()
	{
		return this.delay;
	}

	public void setDelay(int d)
	{
		this.delay = d;
	}

	//deletes previous reading list record, make sure you save it if you need it before running this thread
	public void run()
	{
		this.exit = false; this.readings = new ArrayList<double[]>();
		while (!exit)
		{
			double[] reading = EnergyCheckUtils.energyReadOverDelay(this.delay);
			try { Thread.sleep(this.delay);  } catch (Exception e) {}
			this.readings.add(reading);
		}
	}

	public void end()
	{
		exit = true;
	}

	public String toString()
	{
		String s = "";
		s += "readings: " + this.readings.size() + "\n";
		s += "delay: " + this.delay + "\n";
		int i; for (i = 0; i < this.readings.size()-1; i++) {
			double[] reading = this.readings.get(i);
			s += (i+1) + "\tdram:\t" + reading[0] + "\tcore:\t" + reading[1] + "\tpkg:\t" + reading[2] + "\n";
		}
		double[] reading = this.readings.get(i);
		s += (i+1) + "\tdram:\t" + reading[0] + "\tcore:\t" + reading[1] + "\tpkg:\t" + reading[2];
		return s;
	}

	public static void main(String[] args)
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

	}

}
