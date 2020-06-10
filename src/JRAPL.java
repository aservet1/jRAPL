package jrapl;

import java.lang.reflect.Field;
import java.util.Map;
//import java.lang.invoke.MethodHandles;

public class JRAPL {
	
	/** Call this before doing any JRAPL operations. Sets up the energy collection profile.
	 *  <br>In other words, it initializes data about the system and allocates the proper
	 *  <br>data structures in order to to facilitate the jRAPL interface
	*/
	public native static int ProfileInit();

	
	/** Free all native memory allocated in ProfileInit().
	 *  <br>Call this when done using the jRAPL utilities to clean up resources allocated.
	*/
	public native static void ProfileDealloc();

	
	static {
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
			/*Lookup cl = MethodHandles.privateLookupIn(ClassLoader.class, MethodHandles.lookup());
			VarHandle sys_paths = cl.findStaticVarHandle(ClassLoader.class, "sys_paths", String[].class);
			sys_paths.set(null);*/
		} catch (Exception e) { }

		try {
			NativeUtils.loadLibraryFromJar("/home/alejandro/jRAPL/jRaplSourceCode/libCPUScaler.so");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
