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

    // AsyncEnergyMonitor.c
    public native static void   startMonitor ();
    public native static void   stopMonitor ();
    public native static void   resetMonitor ();
    public native static void   activateMonitor (int samplingRate,int storageType,int size_parameter);
    public native static void   deactivateMonitor ();
    public native static void   writeFileCSVMonitor (String filePath);
    public native static String getLastKSamplesMonitor (int k);
    public native static long[] getLastKTimestampsMonitor (int k);
    public native static int    getNumSamplesMonitor ();
    public native static void   setSamplingRateMonitor (int s);
    public native static int    getSamplingRateMonitor ();

    // not implemented in my C code, but it's EnergyController.java-related code
    //  rapl power cap controller
    private native static double[] GetDramPowerSpec();
    private native static void SetPackagePowerLimit(int socketId, int level, double costomPower);
    private native static void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin);
    private native static void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);
    private native static void SetDramPowerLimit(int socketId, int level, double costomPower);
    private native static void SetPowerLimit(int ENABLE);
    // dvfs controller
    private native static int scale(int freq);
    private native static int[] freqAvailable();
    private native static void SetGovernor(String gov);

	private static void loadNativeLibrary() {
		String nativelib = "/native/libJNIRAPL.so";
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

    // private static NativeAccess instance = new NativeAccess();
    // public static NativeAccess getInstance() {
    //     return instance;
    // }

}
