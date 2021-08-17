package jRAPL;

//@TODO: change name to NativeManager or JNIManager or JNIFunctions or something
/*final */class RaplSingleton { // @TODO: make sure you know exactly what it means to have a final class

    //@TODO: make these methods not static / part of this singleton object if possible, or not.
    //  but it could be a convention where you wrap each one, and have any 'are you subscribed' guard checks on every single function all

    // ArchSpec.c
	public native static int getSocketNum();
	public native static double getWraparoundEnergy();
	public native static String getMicroArchitectureName();
	public native static int getMicroArchitecture();
	public native static String energyStatsStringFormat();
	public native static String getEnergySampleArrayOrder();

    // EnergyCheckUtils.c
    private native /*static*/ void profileInit();
    private native /*static*/ void profileDealloc();
    public native static String energyStatCheck();

    // AsyncEnergyMonitor.c
    public native static void   start();
    public native static void   stop();
    public native static void   reset();
    public native static void   activate(int samplingRate,int storageType,int size_parameter);
    public native static void   deactivate();
    public native static void   writeFileCSV(String filePath);
    public native static String getLastKSamples(int k);
    public native static long[] getLastKTimestamps(int k);
    public native static int    getNumSamples();
    public native static void   setSamplingRate(int s);
    public native static int    getSamplingRate();

    // not implemented in my C code
    private native /*static*/ double[] GetDramPowerSpec();
    private native /*static*/ void SetPackagePowerLimit(int socketId, int level, double costomPower);
    private native /*static*/ void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin);
    private native /*static*/ void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);
    private native /*static*/ void SetDramPowerLimit(int socketId, int level, double costomPower);
    private native /*static*/ void SetPowerLimit(int ENABLE);

    private native /*static*/ int scale(int freq);
    private native /*static*/ int[] freqAvailable();
    private native /*static*/ void SetGovernor(String gov);

	static void loadNativeLibrary() {
		String nativelib = "/native/libJNIRAPL.so";
		try {
			NativeUtils.loadLibraryFromJar(nativelib);
		} catch (Exception e) {
			System.err.println("!! error loading library ! -- " + nativelib);
			e.printStackTrace();
			System.exit(1);
		}
	}

    private volatile int subscribers = 0;
    public synchronized void subscribe() {
        assert (subscribers >= 0);
        if (subscribers == 0) {
            profileInit();
        }
        subscribers++;
    }
    public synchronized void unsubscribe() { //@todo: make sure that synchronized actually means 'this method will run atomically because it is a critical section'
        assert (subscribers >= 0);
        if (subscribers == 1) {
            profileDealloc();
        }
        subscribers--;
    }

    static {
        loadNativeLibrary();
    }

    private RaplSingleton() {}

    private static RaplSingleton instance = new RaplSingleton();
    public static RaplSingleton getInstance() {
        return instance;
    }

}
