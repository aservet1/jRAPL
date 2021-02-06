package jRAPL;

public class EnergyManager
{
	private static boolean libraryLoaded = false;
	private static int energyManagersActive = 0; // counter for shared resource, decrease on dealloc(), when you get to 0 dealloc everything

	// package-private so they can be called in JMH test methods
	native static void profileInit();
	native static void profileDealloc();

	private static void loadNativeLibrary() {
		String nativelib = "/native/libJNIRAPL.so";
		try {
			NativeUtils.loadLibraryFromJar(nativelib);
		} catch (Exception e) {
			System.err.println("!! error loading library ! -- " + nativelib);
			e.printStackTrace();
			System.exit(1);
		}
		libraryLoaded = true;
	}
	public void init() { //get a better name, init might be too generic that confuses other things maybe
		if (!libraryLoaded)
			loadNativeLibrary();
		if (energyManagersActive++ == 0)
			profileInit();
		ArchSpec.init(); // there's definitely a better way of doing this
	}

	public void dealloc() {
		if (--energyManagersActive == 0) profileDealloc();
	}
}
