
package jrapl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class EnergyManager
{
	private static boolean modprobed = false;
	private static boolean libraryLoaded = false;
	private static int energyManagersActive = 0; // counter for shared resource, decrease on dealloc(), when you get to 0 dealloc everything

	//@TODO these should eventually be private methods
	public native static void profileInit();
	public native static void profileDealloc();

	/** Right now only used for 'sudo modprobe msr'.
	*   But can be used for any simple / non compound 
	*   (|&&>;)-like commands. Simple ones.
	*/
	static void execCmd(String command) {
		String s;
		try {
			Process p = Runtime.getRuntime().exec(command);
        		BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
        		BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((s = stdin.readLine()) != null) {
				System.out.println(s); // printing stdout
			} while ((s = stderr.readLine()) != null) {
				System.out.println(s); // printing stderr
			}
		} catch (IOException e) {
        		System.out.println("<<<IOException in execCmd():");
        		e.printStackTrace();
        		System.exit(-1);
        	}
	}
	private static void sudoModprobeMsr() {
		execCmd("sudo modprobe msr");
		modprobed = true;
	}
	private static void loadNativeLibrary() {

		String nativelib = "/NativeLib/libCPUScaler.so";
		try {
			NativeUtils.loadLibraryFromJar(nativelib);
		} catch (Exception e) {
			System.err.println("!! error loading library ! -- " + nativelib);
			e.printStackTrace();
			System.exit(1);
		}
		libraryLoaded = true;
	}
	public void init() //get a better name
	{
		if (!modprobed)
			sudoModprobeMsr();
		if (!libraryLoaded)
			loadNativeLibrary();
		if (energyManagersActive++ == 0)
			profileInit();
		ArchSpec.init(); // there's definitely a better way of doing this
	}

	public void dealloc()
	{
		if (--energyManagersActive == 0) profileDealloc();
	}

}

