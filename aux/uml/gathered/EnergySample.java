package jRAPL
+ abstract class EnergySample
{
+ EnergySample(double[] primitiveSample)
+ EnergySample(EnergySample other)
# csvHeader() static String 
# String csv()
+ getPrimitiveSample() double[] 
- primitiveSample double[] 
- getEnergy(int socket, int index) double 
+ getCore(int socket) double 
+ getCore() double 
+ getGpu(int socket) double 
+ getGpu() double 
+ getPackage(int socket) double 
+ getPackage() double 
+ getDram(int socket) double 
+ getDram() double 
}
