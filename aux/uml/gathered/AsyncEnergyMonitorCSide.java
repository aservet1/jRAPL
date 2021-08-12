package jRAPL

class AsyncEnergyMonitorCSide extends AsyncEnergyMonitor

- startNative() void 
- stopNative() void 
- resetNative() void 
- activateNative(int samplingRate,int storageType,int size_parameter) void 
- deactivateNative() void 
- writeFileCSVNative(String filePath) void 
- getLastKSamplesNative(int k) String 
- longgetLastKTimestampsNative(int k) [] 
- getNumSamplesNative() int 
- setSamplingRateNative(int s) void 
- getSamplingRateNative() int 
- DYNAMIC_ARRAY_STORAGE   final int 
- LINKED_LIST_STORAGE  final int 
- samplingRate int 
- storageType int 
- initialSize  int 
+ AsyncEnergyMonitorCSide()
+ AsyncEnergyMonitorCSide(int s, String storageTypeString, int size)
+ AsyncEnergyMonitorCSide(String storageTypeString)
