package jrapl;

import java.lang.reflect.Field;

public class PerfCheckUtils {

    
    /** <h1> DOCUMENTATION OUT OF DATE </h1> 
     *
     * @param
     * @param
     */	
    public native static void perfInit(int numEvents, int isSet);       ///need to doc -- see perfCheck.c
    /** <h1> DOCUMENTATION OUT OF DATE </h1> 
     *
     * @param
     */	
    public native static void singlePerfEventCheck(String eventNames);  ///need to doc -- see perfCheck.c
    /** <h1> DOCUMENTATION OUT OF DATE </h1> 
     *
     * @param
     */	
	public native static void groupPerfEventsCheck(String eventNames);  ///need to doc -- see perfCheck.c
    /** <h1> DOCUMENTATION OUT OF DATE </h1> 
     *
     */		
    public native static void perfEnable();                             ///need to doc -- see perfCheck.c
    /** <h1> DOCUMENTATION OUT OF DATE </h1> 
     *
     */	
	public native static void perfDisable();                            ///need to doc -- see perfCheck.c
    /** <h1> DOCUMENTATION OUT OF DATE </h1> 
     *
     * @param
     * @param
     */	
	public native static void perfSingleRead(int id, long[] buffer);    ///need to doc -- see perfCheck.c
    /** <h1> DOCUMENTATION OUT OF DATE </h1> 
     *
     * @param
     */	
	public native static void perfMultRead(long[] buffer);              ///need to doc -- see perfCheck.c
    /** <h1> DOCUMENTATION OUT OF DATE </h1> 
     *
     * @param
     */	
	public native static long processSingleValue(long[] buffer);        ///need to doc -- see perfCheck.c
    /** <h1> DOCUMENTATION OUT OF DATE </h1> 
     *
     * @param
     */	
	public native static long[] processMultiValue(long[] buffer);       ///need to doc -- see perfCheck.c

    /** <h1> DOCUMENTATION OUT OF DATE </h1> Number of events detected from singlePerfEventsCheck() or groupPerfEventsCheck()
    */
	public static int eventNum = 0;
	//For testing, Make the variable not be optimized as static
	static int[] test = new int[100000000];

	static {
		System.setProperty("java.library.path", System.getProperty("user.dir"));
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (Exception e) { }

		System.loadLibrary("perfCheck");
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	 * Initialize perf check utilities
	 *
	 * @param eventNames String names of hardware counters to be checked
	 * @param isGrouped Do you want get @eventNames counters as single read?
	 */
	public static void perfEventInit(String eventNames, boolean isGrouped) {
		int setGroup;
		String[] eventName = eventNames.split(",");
		eventNum = eventName.length;

		setGroup = isGrouped ? 1 : 0;
		perfInit(eventNum, setGroup);

		if(isGrouped) {
			groupPerfEventsCheck(eventNames);
		} else {
			singlePerfEventCheck(eventNames);
		}

		perfEnable();
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	 * Get multiple perf counter values with single read
	 */
	public static long[] getMultiPerfCounter() {
		long[] buffer = new long[3 + 2 * eventNum];
		long[] results = new long[eventNum];

		if(eventNum > 0) {
			perfMultRead(buffer);
			results = processMultiValue(buffer);
		} else {
			System.err.println("event number is 0, should call perfEventInit first!");
			System.exit(-1);
		}

		return results;
	}

	/** <h1> DOCUMENTATION OUT OF DATE </h1>
	 * Get one perf counter value a time
	 */
	public static long[] getSinglePerfCounter() {
		long[] buffer = new long[3];
		long[] results = new long[eventNum];

		if(eventNum > 0) {
			for(int i = 0; i < results.length; i++) {
				perfSingleRead(i, buffer);
				results[i] = processSingleValue(buffer);
			}
		} else {
			System.err.println("event number is 0, should call perfEventInit first!");
			System.exit(-1);
		}
		return results;
	}

	public static void main(String[] args) {
		long[] preamble;
		long[] epilogue;
		String counters = "cache-misses,cache-references";

//		perfEventInit(counters, false);
		perfEventInit(counters, true);

//		preamble = getSinglePerfCounter();
		preamble = getMultiPerfCounter();
		System.out.println("Testing...");

		for(int i = 0; i < test.length; i++) {
			test[i] *= test[i] + i;
		}
		System.out.println("Finish");
//		try {
//			Thread.sleep(10000);
//		} catch(Exception e) {
//		}
//		epilogue = getSinglePerfCounter();
		epilogue = getMultiPerfCounter();

		System.out.println("cache misses is: " + (epilogue[0] - preamble[0]));
		System.out.println("cache references is: " + (epilogue[1] - preamble[1]));

		perfDisable();
	}
}
