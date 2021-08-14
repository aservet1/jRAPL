package jRAPL;

public class EnergyManager
{
	private boolean active = false;

	private static boolean libraryLoaded = false;

	//TODO make this atomic, or make a synchronized increment function for thread safety
	private static int energyManagersActive = 0; // counter for shared resource

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

	static boolean isLibraryLoaded() {
		return libraryLoaded;
	}

	public void activate() {
		if (active) {
			System.err.println(
				"Error: "
				+ getClass().getName()
				+ "@"
				+ Integer.toHexString(hashCode())
				+ " already activated."
				+ " Double activate is not allowed. Exiting program."
			);
			System.exit(1);
		}

		if (!isLibraryLoaded()) {
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
		if (!active) {
			System.err.println(
				"Error: "
				+ getClass().getName()
				+ "@"
				+ Integer.toHexString(hashCode())
				+ " already deactivated."
				+ " Double deactivate is not allowed. Exiting program."
			);
			System.exit(1);
		}

		active = false;
		energyManagersActive -= 1;

		if (energyManagersActive == 0) profileDealloc();
	}
}
