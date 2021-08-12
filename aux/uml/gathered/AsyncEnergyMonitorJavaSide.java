package jRAPL;
AsyncEnergyMonitorJavaSide extends AsyncEnergyMonitor implements Runnable {
+ AsyncEnergyMonitorJavaSide()
+ AsyncEnergyMonitorJavaSide(int s)
- ArrayList<String> samples;
- samplingRate int ;
- exit volatile boolean ;
- t  Thread ;
}
