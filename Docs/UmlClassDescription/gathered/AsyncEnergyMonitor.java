abstract class AsyncEnergyMonitor extends EnergyMonitor 
 
# monitorStartTime Instant 
# monitorStopTime Instant  
+ getLifetime() Duration  
+ start() void 
+ stop() void 
+ reset() void 
+ writeToFile(String fileName) abstract void 
+ getLastKSamples(int) abstract String[] 
+ getLastKTimestamps(int) abstract Instant[] 
+ getLastKSamples_Objects(int) EnergyStats[][] 
+ getLastKSamples_Arrays(int) double[][] 
} 
