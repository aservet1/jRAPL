package jrapl_testing; // TODO make a jrapl_testutils package for diagnostics as opposed to user work

import jrapl.*;

class ThreadTesting
{
	//TODO better way to keep thread alive than sleeping?

	// insert all native calls here...

	private static void writeToFile(String filePath)
	{
		System.out.println("writing to "+filePath);
	}

	public static void runJavaVersion()
	{
		System.out.println("running java");
	}

	public static void runCVersion()
	{
		System.out.println("running c");
	}

	public static void main(String[] args)
	{
		if (args.length == 0 || (!args[0].equalsIgnoreCase("java") && !args[0].equalsIgnoreCase("c"))) {
			System.out.println("usage: java ThreadTesting [java|c]");
			System.exit(1);
		}
		if (args[0].equalsIgnoreCase("java"))
		{
			runJavaVersion();
			String javaFile = "./extra/java.dump";
			writeToFile(javaFile);
		}
		if (args[0].equalsIgnoreCase("c"))
		{
			runCVersion();
			String cFile = "./extra/c.dump";
			writeToFile(cFile);
		}
	}
}
