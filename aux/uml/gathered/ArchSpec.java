package jRAPL;
public final class ArchSpec {
public static final int NUM_SOCKETS;
public static final int NUM_STATS_PER_SOCKET;
public static final double RAPL_WRAPAROUND;
public static final int MICRO_ARCHITECTURE;
public static final String MICRO_ARCHITECTURE_NAME;
public native static int getSocketNum();
public native static double getWraparoundEnergy();
public native static String getMicroArchitectureName();
public native static int getMicroArchitecture();
public native static String energyStatsStringFormat();
public static final int DRAM_IDX;
public static final int GPU_IDX;
public static final int CORE_IDX;
public static final int PKG_IDX;
public static final boolean DRAM_SUPPORTED;
public static final boolean GPU_SUPPORTED;
public static final boolean CORE_SUPPORTED;
public static final boolean PKG_SUPPORTED;
static
public static void init()
public static String infoString()
}
