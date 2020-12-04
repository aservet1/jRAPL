package jrapltesting;

import jrapl.*;

public class ThreadTesting
{
	/*private static void writeToFile(String data, String filePath)
	{
		System.out.println("writing to "+filePath);
		FileWriter writer = null;
		try {
			writer = new FileWriter(filePath);
			writer.write(data+"\n");
            writer.close();
		} catch (Exception e) {
			System.out.println("error writing to "+filePath);
		}
	}*/
	

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
		if (args.length < 3 || !args[0].equalsIgnoreCase("java") && !args[0].equalsIgnoreCase("c"))
		{
			System.out.println("usage: java jrapltesting.ThreadTesting <java|c> <monitor lifetime (ms)> <monitor delay> <outfile>");
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
		if (args[0].equalsIgnoreCase("c"))
		{
			run(new AsyncEnergyMonitorCSide(delay),lifetime,outfile);
		}
	
	}
}




