package jrapltesting;

import jrapl.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;

class ThreadTesting //extends JRAPL
{
	//TODO better way to keep thread alive than sleeping?

	// insert all native calls here...

	private static void writeToFile(String data, String filePath)
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
	}

	public static String runJavaVersion()
	{
		System.out.println("running java");
		String sourceFile = "./extra/dump-j.tmp";
		Scanner fileScan = null;
		String data = "";
		try {
			fileScan = new Scanner(new File(sourceFile));
			while (fileScan.hasNextLine()) {
				String line = fileScan.nextLine();
				data += line + "\n";
			}
            fileScan.close();
		} catch(Exception e) {
			System.out.println(sourceFile+" not found or something");
		}
		return data;
	}

	public static String runCVersion()
	{
		System.out.println("running c");
		String sourceFile = "./extra/dump-j.tmp";
		Scanner fileScan = null;
		String data = "";
		try {
			fileScan = new Scanner(new File(sourceFile));
			while (fileScan.hasNextLine()) {
				String line = fileScan.nextLine();
				data += line + "\n";
			}
		} catch(Exception e) {
			System.out.println(sourceFile+" not found or something");
		}
		return data;
	}

	public static void main(String[] args)
	{
		if (args.length == 0 || (!args[0].equalsIgnoreCase("java") && !args[0].equalsIgnoreCase("c"))) {
			System.out.println("usage: java ThreadTesting [java|c]");
			System.exit(1);
		}
		if (args[0].equalsIgnoreCase("java"))
		{
			String data = runJavaVersion();
			//System.out.println(data);
			String javaFile = "./extra/java.dump";
			writeToFile(data, javaFile);
		}
		if (args[0].equalsIgnoreCase("c"))
		{
			String data = runCVersion();
			//System.out.println(data);
			String cFile = "./extra/c.dump";
			writeToFile(data, cFile);
		}
	}
}
