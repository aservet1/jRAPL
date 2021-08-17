package jRAPL;

//TODO: change name to NativeManager or something
final class RaplSingleton { // TODO: make sure you know exactly what it means to have a final class

    // ArchSpec.c
	public native static int getSocketNum();
	public native static double getWraparoundEnergy();
	public native static String getMicroArchitectureName();
	public native static int getMicroArchitecture();
	public native static String energyStatsStringFormat();

    // EnergyCheckUtils.c
    private native static void profileInit();
    private native static void profileDealloc();

    // AsyncEnergyMonitor.c
    public native static void start();
    public native static void stop();
    public native static void reset();
    public native static void activate(int samplingRate,int storageType,int size_parameter);
    public native static void deactivate();
    public native static void writeFileCSV(String filePath);
    public native static String getLastKSamples(int k);
    public native static long[] getLastKTimestamps(int k);
    public native static int getNumSamples();
    public native static void setSamplingRate(int s);
    public native static int getSamplingRate();

    // not implemented in my C code
    private native static double[] GetDramPowerSpec();
    private native static void SetPackagePowerLimit(int socketId, int level, double costomPower);
    private native static void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin);
    private native static void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);
    private native static void SetDramPowerLimit(int socketId, int level, double costomPower);
    private native static void SetPowerLimit(int ENABLE);

    private native static int scale(int freq);
    private native static int[] freqAvailable();
    private native static void SetGovernor(String gov);

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

    private static volatile int subscribers;
    public static void subscribe() {
        assert (subscribers >= 0);
        if (subscribers == 0) {
            profileInit();
        }
        subscribers++;
    }
    public static void unsubscribe() {
        assert (subscribers >= 0);
        if (subscribers == 1) {
            profileDealloc();
        }
        subscribers--;
    }

    static {
        loadNativeLibrary();
        subscribers = 0;
    }

    private RaplSingleton() {}

    private static RaplSingleton instance = null;
    public static RaplSingleton getInstance() {
        if (instance != null) instance = new RaplSingleton();
        return instance;
    }

}
