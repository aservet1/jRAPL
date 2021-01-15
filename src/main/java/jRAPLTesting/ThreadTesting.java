package jRAPLTesting;

import jRAPL.*;

public class ThreadTesting
{
	//TODO better way to keep thread alive than sleeping?

	private static void run(AsyncEnergyMonitor mon, int lifetime, String outfile)
	{
		mon.init();
		mon.start();
		try{
			Thread.sleep(lifetime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mon.stop();
		mon.writeToFile(outfile);
		mon.dealloc();
	}

	public static void main(String[] args)
	{
		if (args.length < 3)
		{
			System.out.println("usage: java jrapltesting.ThreadTesting <java|c-dynamicarray|c-linkedlist> <monitor lifetime (ms)> <monitor delay> <outfile>");
			System.out.println("  -- if no argument for outfile, prints to stdout");
			System.exit(2);
		}

		int lifetime = Integer.parseInt(args[1]);
		int delay = Integer.parseInt(args[2]);
		String outfile = (args.length == 4) ? args[3] : null;

		if (args[0].equalsIgnoreCase("java"))
		{
			run(new AsyncEnergyMonitorJavaSide(delay),lifetime,outfile);
		}
		else if (args[0].equalsIgnoreCase("c-dynamicarray"))
		{
			run(new AsyncEnergyMonitorCSide(delay,"DYNAMIC_ARRAY"),lifetime,outfile);
		}
		else if (args[0].equalsIgnoreCase("c-linkedlist"))
		{
			run (new AsyncEnergyMonitorCSide(delay,"LINKED_LIST"),lifetime,outfile);
		}
		else
		{
			System.out.println("usage: java jrapltesting.ThreadTesting <java|c-dynamicarray|c-linkedlist> <monitor lifetime (ms)> <monitor delay> <outfile>");
			System.out.println("  -- if no argument for outfile, prints to stdout");
			System.exit(2);
		}
	
	}
}


