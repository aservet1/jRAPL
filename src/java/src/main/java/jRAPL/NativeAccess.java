package jRAPL;

//@TODO: change name to NativeManager or JNIManager or JNIFunctions or something
final class NativeAccess { // @TODO: make sure you know exactly what it means to have a final class

    //@TODO: make these methods not static / part of this singleton object if possible, or not.
    //  but it could be a convention where you wrap each one, and have any 'are you subscribed' guard checks on every single function all

    // ArchSpec.c
	public native static int getSocketNum();
	public native static double getWraparoundEnergy();
	public native static double getDramWraparoundEnergy();
	public native static String getMicroArchitectureName();
	public native static int getMicroArchitecture();
	public native static String energyStatsStringFormat();
	public native static String getEnergySampleArrayOrder();

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
    public static synchronized void unsubscribe() { //@todo: make sure that synchronized actually means 'this method will run atomically because it is a critical section'
        assert (subscribers >= 0);
        if (subscribers == 1) {
            profileDealloc();
        }
        subscribers--;
    }

    private NativeAccess() {}

}
