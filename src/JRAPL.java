package jrapl;

import java.lang.reflect.Field;
import java.util.Map;
//import java.lang.invoke.MethodHandles;

public class JRAPL {
	
	//protected static int wraparoundValue;
	//protected static int socketNum;

	/** Documentation not done. See CPUScaler.c for source
	*  Initializes the energy profile of the system. To be called before accessing any jRAPL utility.
	*  Information initialized (stored entirely in static global variables on the C side):
	*      CPU Model
	*      Number of CPU sockets
	*      Array of file handles for MSR readings
	*
	*  @return wraparoundValue -- 
	*
	*/
 	//public native static int ProfileInit();
  
 	/** Finds the number of CPU sockets the system has
 	*  @return number of CPU sockets
 	*/
	//public native static int GetSocketNum();


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
		//wraparoundValue = ProfileInit();
		//socketNum = GetSocketNum();
	}
}
