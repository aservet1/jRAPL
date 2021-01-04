abstract class EnergySample 
 
# socket final int 
# stats final double[]
# timestamp Instant 
+ EnergySample(int, double[], Instant) 
+ EnergySample(int, double[]) 
+ getSocket() int 
+ getCore() double 
+ getGpu() double 
+ getPackage() double 
+ getDram() double  
+ dump() String 
+ setTimestamp(Instant ts) void 
+ toString() String  
 
