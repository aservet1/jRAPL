public class AsyncEnergyMonitorJavaSide extends AsyncEnergyMonitor implements Runnable
- ArrayList<String> samples;
- int samplingRate;
- volatile boolean exit 
- Thread t
# timestamps final ArrayList<Instant> 
+ AsyncEnergyMonitorJavaSide()
+ AsyncEnergyMonitorJavaSide(int)
+ run() void 
+ start() void 
+ stop() void 
+ reset() void 
+ getLastKSamples(int) String[] 
+ getLastKTimestamps(int) Instant[] 
+ getSamplingRate() int 
+ setSamplingRate(int) void 
+ getNumReadings() int 
+ writeToFile(String) void 
+ toString() String 
