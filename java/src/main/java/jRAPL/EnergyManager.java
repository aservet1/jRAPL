package jRAPL;

public class EnergyManager
{
	private boolean active = false;

	private static boolean libraryLoaded = false;
	private static int energyManagersActive = 0; // counter for shared resource, decrease on deactivate(), when you get to 0 dealloc everything

	// package-private so they can be called in JMH test methods
	native static void profileInit();
	native static void profileDealloc();

	static void loadNativeLibrary() {
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

	public void activate() { //get a better name, init might be too generic that confuses other things maybe
		assert !active;

		if (!libraryLoaded) {
			loadNativeLibrary();
			ArchSpec.init(); // there's definitely a better way of doing this
		}
		if (energyManagersActive == 0) {
			profileInit();
		}

		active = true;
		energyManagersActive += 1;
	}

	public void deactivate() {
		assert active;

		active = false;
		energyManagersActive -= 1;

		if (energyManagersActive == 0) profileDealloc();
	}
}
