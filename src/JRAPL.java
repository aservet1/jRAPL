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

	//public JRAPL() {} // private constructor -- never initialized
	
	/** <h1> DOCUMENTATION OUT OF DATE </h1> Call this before doing any JRAPL operations. 
	 *  <br>Initializes data about the system and allocates the proper
	 *  data structures in order to to facilitate the jRAPL interface
	*/
	public native static int ProfileInit();

	
	/** <h1> DOCUMENTATION OUT OF DATE </h1> Free all native memory allocated in ProfileInit().
	 *  <br>Call this when done using the jRAPL utilities to clean up resources allocated.
	*/
	public native static void ProfileDealloc();


	/** <h1> DOCUMENTATION OUT OF DATE </h1> Gets the wraparound energy value that I still don't e n t i r e l y understand
	*/
	public native static int GetWrapAroundEnergy();	


	/** <h1> DOCUMENTATION OUT OF DATE </h1> Number of CPU sockets the current system has
	*/
	public static final int NUM_SOCKETS;

	/** <h1> DOCUMENTATION OUT OF DATE </h1> Energy wrap around value (I think it's for if an energy sample is taken over the MSR overflow/reset)
	*/
	public static final int ENERGY_WRAP_AROUND;
	
	static {
		/*try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
			//MethodHandles.Lookup cl = MethodHandles.privateLookupIn(ClassLoader.class, MethodHandles.lookup());
			//VarHandle sys_paths = cl.findStaticVarHandle(ClassLoader.class, "sys_paths", String[].class);
			//sys_paths.set(null);
		} catch (Exception e) { }*/

		try {
			//NativeUtils.loadLibraryFromJar("/home/alejandro/jRAPL/src/libCPUScaler.so");
			System.load("/home/alejandro/jRAPL/src/libCPUScaler.so");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		NUM_SOCKETS = 1; //TODO //ArchitectureSpecifications.GetSocketNum();
		ENERGY_WRAP_AROUND = ProfileInit();
	}
}
