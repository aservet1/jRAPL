
package jrapl;

public class EnergyManager
{
	//@TODO these should eventually be private methods
	public native static void profileInit();
	public native static void profileDealloc();
	private static void loadLibrary() {

		// do NOT delete this commented sections in this !! it's going to be useful when i actually need to load library from jar
		
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
	}
	public void init() //get a better name
	{
		loadLibrary();
		profileInit();
		ArchSpec.init(); // there's definitely a better way of doing this
	}

	public void dealloc()
	{
		profileDealloc();
	}

}

