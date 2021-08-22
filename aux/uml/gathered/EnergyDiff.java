EnergyDiff extends EnergySample
+ EnergyDiff(double[] primitiveSample, Duration elapsedTime)
- Duration elapsedTime = null;
+ setElapsedTime(Duration elapsed) void 
+ getElapsedTime() Duration 
+ csvHeader()  String 
+ between(EnergyStats before, EnergyStats after)  EnergyDiff 
