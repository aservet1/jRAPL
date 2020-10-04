package jrapl;

import java.lang.reflect.Field;
import java.util.Map;
//import java.lang.invoke.MethodHandles;
//import java.lang.invoke.VarHandle;

/** <h1> DOCUMENTATION OUT OF DATE </h1>
*	Superclass for all JRAPL-using classes. Loads the native jRAPL library into memory
*	and allows access to all native calls. Also contains functions to generally facilitate
*	the JRAPL interface.
*/
public class JRAPL {
	
	/** <h1> DOCUMENTATION OUT OF DATE </h1> Call this before doing any JRAPL operations. 
	 *  <br>Initializes data about the system and allocates the proper
	 *  data structures in order to to facilitate the jRAPL interface
	*/
	public native static int ProfileInit();
	
	/** <h1> DOCUMENTATION OUT OF DATE </h1> Free all native memory allocated in ProfileInit().
	 *  <br>Call this when done using the jRAPL utilities to clean up resources allocated.
	*/
	public native static void ProfileDealloc();

	
	static {
		/*try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
			//MethodHandles.Lookup cl = MethodHandles.privateLookupIn(ClassLoader.class, MethodHandles.lookup());
			//VarHandle sys_paths = cl.findStaticVarHandle(ClassLoader.class, "sys_paths", String[].class);
			//sys_paths.set(null);
		} catch (Exception e) { }*/

		String nativelib = "/home/alejandro/jRAPL/src/libCPUScaler.so";
		try {
			//NativeUtils.loadLibraryFromJar("libCPUScaler.so");
			System.load(nativelib);
		} catch (Exception e) {
			System.err.println("ERROR LOADING LIBRARY " + nativelib);
			e.printStackTrace();
			System.exit(1);
		}
		//ProfileInit();
	}
}
