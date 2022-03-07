package jRAPL;

final class NativeAccess {
    // ArchSpec.c
	public native static int getSocketNum();
	public native static double getWraparoundEnergy();
	public native static double getDramWraparoundEnergy();
	public native static String getMicroArchitectureName();
	public native static int getMicroArchitectureID();

    // EnergyCheckUtils.c
    public native static void profileInit();
    public native static void profileDealloc();
    public native static String energyStatCheck();
    public native static void setCSVDelimiter(char c);

	private static void loadNativeLibrary() {
		String nativelib = "/jniRAPL/librapl_jni.so";
		try {
			NativeUtils.loadLibraryFromJar(nativelib);
		} catch (Exception e) {
			System.err.println("!! error loading library ! -- " + nativelib);
			e.printStackTrace();
			System.exit(1);
		}
	}

    static {
        loadNativeLibrary();
    }

    private static volatile int subscribers = 0;
    public static synchronized void subscribe() {
        assert (subscribers >= 0);
        if (subscribers == 0) {
            profileInit();
        }
        subscribers++;
    }
    public static synchronized void unsubscribe() {
        assert (subscribers >= 0);
        if (subscribers == 1) {
            profileDealloc();
        }
        subscribers--;
    }

    private NativeAccess() {}
}
