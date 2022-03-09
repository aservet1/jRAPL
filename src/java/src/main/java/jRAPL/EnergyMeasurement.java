package jRAPL;

import java.time.Instant;
import java.time.Duration;

public final class EnergyMeasurement {

    private double[] dramPerSocket;
    private double[] pp0PerSocket ;
    private double[] pp1PerSocket ;
    private double[] pkgPerSocket ;

	private Instant startTimestamp;
	private Instant stopTimestamp;

	private EnergyMeasurement(double[] dram, double[] pp0, double[] pp1, double[] pkg, Instant startTimestamp, Instant stopTimestamp) {
        this.dramPerSocket  = dram;
        this.pp0PerSocket   = pp0;
        this.pp1PerSocket   = pp1;
        this.pkgPerSocket   = pkg;
		this.startTimestamp = startTimestamp;
		this.stopTimestamp  = stopTimestamp;
	}

    public double getDRAM(int socket) { return dramPerSocket[socket]; }
    public double getPP0(int socket)  { return pp0PerSocket[socket];  }
    public double getPP1(int socket)  { return pp1PerSocket[socket];  }
    public double getPKG(int socket)  { return pkgPerSocket[socket];  }

    /* The get*() with no socket argument passed is a total for that
        power component across all sockets. */
    public double getDRAM() {
        double total = 0;
        for(int socket = 0; socket < ArchSpec.NUM_SOCKETS; socket++) {
            if (dramPerSocket[socket] < 0) return -1;
            total += dramPerSocket[socket];
        }
        return total;
    }
    public double getPP0() {
        double total = 0;
        for(int socket = 0; socket < ArchSpec.NUM_SOCKETS; socket++) {
            if (pp0PerSocket[socket] < 0) return -1;
            total += pp0PerSocket[socket];
        }
        return total;
    }
    public double getPP1() {
        double total = 0;
        for(int socket = 0; socket < ArchSpec.NUM_SOCKETS; socket++) {
            if (pp1PerSocket[socket] < 0) return -1;
            total += pp1PerSocket[socket];
        }
        return total;
    }
    public double getPKG() {
        double total = 0;
        for(int socket = 0; socket < ArchSpec.NUM_SOCKETS; socket++) {
            if (pkgPerSocket[socket] < 0) return -1;
            total += pkgPerSocket[socket];
        }
        return total;
    }

    /** Time duration of this energy measurement. Can be used to calculate power.*/
	public Duration getTimeElapsed() {
		return Duration.between(startTimestamp, stopTimestamp);
	}

    /** Timestamp of the beginning of this measurement */
	public Instant getStartTimestamp() {
		return this.startTimestamp;
	}

    /** Timestamp of the end of this measurement */
	public Instant getStopTimestamp() {
		return this.stopTimestamp;
	}

	public static String csvHeader() {
        String delim = EnergyMonitor.getCSVDelimiter();
        String fmt = "dram_socket%d%spp0_socket%d%spp1_socket%d%spkg_socket%d%s";
        String header = "";
        for (int socket = 0; socket < ArchSpec.NUM_SOCKETS; ++socket) {
            header += String.format(
                fmt,
                socket,delim,
                socket,delim,
                socket,delim,
                socket,delim
            );
        }
		return header + "start_timestamp" + delim + "time_elapsed";
	}

	public String csv() {
        String delim = EnergyMonitor.getCSVDelimiter();
        String fmt = "%.6f%s%.6f%s%.6f%s%.6f%s";
		String csv = "";
        for (int socket = 0; socket < ArchSpec.NUM_SOCKETS; ++socket) {
            csv += String.format(
                fmt,
                getDRAM(socket),delim,
                getPP0(socket),delim,
                getPP1(socket),delim,
                getPKG(socket),delim
            );
        }
        return csv + getStartTimestamp().toEpochMilli() + delim + getTimeElapsed().toMillis();
	}

    public String toString() {
        return csv();
    }

    private static boolean bothPositive(double x, double y) {
        return !(x < 0 || y < 0);
    }

	public static EnergyMeasurement between(EnergySample before, EnergySample after) {
        // see src/native/JNI/EnergyCheckUtils.c for the source of truth behind these indices
        int dram_i =  0;
        int pp0_i  =  1;
        int pp1_i  =  2;
        int pkg_i  =  3;

        double[] dram = new double[ArchSpec.NUM_SOCKETS];
        double[] pp0  = new double[ArchSpec.NUM_SOCKETS];
        double[] pp1  = new double[ArchSpec.NUM_SOCKETS];
        double[] pkg  = new double[ArchSpec.NUM_SOCKETS];
       
        double[] raplCountersAfter  = after.getRaplCounters();
        double[] raplCountersBefore = before.getRaplCounters();

        for(int socket = 0; socket < ArchSpec.NUM_SOCKETS; ++socket) {
            if (bothPositive(raplCountersAfter[socket+dram_i], raplCountersBefore[socket+dram_i])) {
                dram[socket] = raplCountersAfter[socket+dram_i] - raplCountersBefore[socket+dram_i];
                if (dram[socket] < 0 ) dram[socket] += ArchSpec.DRAM_RAPL_WRAPAROUND;
            } else {
                dram[socket] = -1;
            }
            if (bothPositive(raplCountersAfter[socket+pp0_i], raplCountersBefore[socket+pp0_i])) {
                pp0[socket] = raplCountersAfter[socket+pp0_i] - raplCountersBefore[socket+pp0_i];
                if (pp0[socket] < 0 ) pp0[socket] += ArchSpec.RAPL_WRAPAROUND;
            } else {
                pp0[socket] = -1;
            }
            if (bothPositive(raplCountersAfter[socket+pp1_i], raplCountersBefore[socket+pp1_i])) {
                pp1[socket] = raplCountersAfter[socket+pp1_i] - raplCountersBefore[socket+pp1_i];
                if (pp1[socket] < 0 ) pp1[socket] += ArchSpec.RAPL_WRAPAROUND;
            } else { 
                pp1[socket] = -1;
            }
            if (bothPositive(raplCountersAfter[socket+pkg_i], raplCountersBefore[socket+pkg_i])) {
                pkg[socket] = raplCountersAfter[socket+pkg_i] - raplCountersBefore[socket+pkg_i];
                if (pkg[socket] < 0 ) pkg[socket] += ArchSpec.RAPL_WRAPAROUND;
            } else {
                pkg[socket] = -1;
            }
        }

        return new EnergyMeasurement (
			dram, pp0, pp1, pkg,
			before.getTimestamp(),
			after.getTimestamp()
		);
	}
}
