
package jRAPL;

class natives_do_not_use_this_is_jusst_a_reference {
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
}
