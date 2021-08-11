package jRAPL;
import java.time.Duration;
import java.time.Instant;
public class DriverTmp {
public native static void ctimeStart();
public native static void ctimeStop();
public native static long ctimeElapsedUsec();
static double mean(long[] list)
static double stdev(long[] list)
public static void main(String[] args) throws InterruptedException
}
