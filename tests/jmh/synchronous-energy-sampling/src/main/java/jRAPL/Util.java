package jRAPL;

import org.openjdk.jmh.infra.Blackhole;

public class Util {
    public static void busyWait(Blackhole b) {
        int i = 0;
        /* Busy wait so that the MSRs don't shut down from reading too often.
         * Thousands of readings with no rest between them causes them to reject
         * the reading them.
         */
        while(i < 100) b.consume(i++);
    }
}
