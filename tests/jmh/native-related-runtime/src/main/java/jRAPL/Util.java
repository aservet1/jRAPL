package jRAPL;

import org.openjdk.jmh.infra.Blackhole;

public class Util {
    public static void busyWait(Blackhole b) {
        int i = 0;
        /* Busy wait so that we dont break the msrs*/
        while(i < 100) b.consume(i++);
    }
}
