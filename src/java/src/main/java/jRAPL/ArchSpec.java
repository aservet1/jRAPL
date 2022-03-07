package jRAPL;

public final class ArchSpec {
	public static final int NUM_SOCKETS;	
	public static final double RAPL_WRAPAROUND;
	public static final double DRAM_RAPL_WRAPAROUND;
	public static final int MICRO_ARCHITECTURE_ID;
	public static final String MICRO_ARCHITECTURE_NAME;

	public static void init() {} // do-nothing function to trigger the static block...probably a better way of doing this
	static {
        NativeAccess.subscribe();

		MICRO_ARCHITECTURE_ID   = NativeAccess.getMicroArchitectureID();
		MICRO_ARCHITECTURE_NAME = NativeAccess.getMicroArchitectureName();

		NUM_SOCKETS = NativeAccess.getSocketNum();
		RAPL_WRAPAROUND = NativeAccess.getWraparoundEnergy();
        DRAM_RAPL_WRAPAROUND = NativeAccess.getDramWraparoundEnergy();

	    NativeAccess.unsubscribe();
	}
	
	public static String infoString() {
		return String.join(
			"\n",
			"MICRO_ARCHITECTURE_ID: " + Integer.toHexString(MICRO_ARCHITECTURE_ID),
			"MICRO_ARCHITECTURE_NAME: " + MICRO_ARCHITECTURE_NAME,
			"",
			"NUM_SOCKETS: " + NUM_SOCKETS,
			"RAPL_WRAPAROUND: " + RAPL_WRAPAROUND,
			"DRAM_RAPL_WRAPAROUND: " + DRAM_RAPL_WRAPAROUND
		);
	}
}
