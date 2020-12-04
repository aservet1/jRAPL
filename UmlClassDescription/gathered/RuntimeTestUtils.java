class RuntimeTestUtils 
 
- printFunctionTimeRecord(long[], String) void 
- printMSRReadTimeRecord(long[][], String) void 
- usageAbort() void 
+ initCSideTiming() void 
+ deallocCSideTiming() void 
+ timeMethod(Runnable) long 
+ timeMethodMultipleIterations(Runnable, String, int) void 
+ usecTimeProfileInit() long 
+ usecTimeGetSocketNum() long 
+ usecTimeEnergyStatCheck(int) long 
+ usecTimeProfileDealloc() long 
+ usecTimeMSRRead(int) long[] 
+ timeAllMSRReads(int) void 
+ timeAllCFunctions(int) void 
 
