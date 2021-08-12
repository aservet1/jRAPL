package jRAPL
abstract class AsyncEnergyMonitor extends EnergyMonitor
# monitorStartTime Instant  
# monitorStopTime Instant  
# isRunning boolean  
# samplingRat int e
+ getNumSamples() abstract int 
+ setSamplingRate(int s) abstract void 
+ getSamplingRate() abstract int 
+ getLifetime() Duration 
+ start() void 
+ stop() void 
+ reset() void 
+ abstract Instant[] getLastKTimestamps(int k)
+ abstract String[] getLastKSamples(int k)
+ getLastKSamples_Objects(int k) EnergyStats[] 
+ getLastKSamples_Arrays(int k) double[][] 
+ isRunning() boolean 
+ writeFileCSV(String fileName) abstract void 
+ writeFileMetadata(String fileName) void 
