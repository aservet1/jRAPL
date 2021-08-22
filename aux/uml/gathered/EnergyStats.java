package jRAPL;

EnergyStats extends EnergySample
- Instant timestamp;
+ EnergyStats(double[] primitiveSample, Instant ts)
+ EnergyStats(EnergyStats other)
+ csvHeader() String 
+ getTimestamp() Instant 
+ setTimestamp(Instant ts) void 
