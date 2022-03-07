package jRAPL;

import java.time.Instant;
import java.time.Duration;

public final class EnergyMeasurement {

    private int[] dramPerSocket = new int[ArchSpec.NUM_SOCKETS];
    private int[] pp0PerSocket  = new int[ArchSpec.NUM_SOCKETS];
    private int[] pp1PerSocket  = new int[ArchSpec.NUM_SOCKETS];
    private int[] pkgPerSocket  = new int[ArchSpec.NUM_SOCKETS];

	private Instant startTimestamp;
	private Instant stopTimestamp;

	private EnergyMeasurement(double[] measurements, Instant startTimestamp, Instant stopTimestamp) {
        for(int socket = 0; socket < ArchSpec.NUM_SOCKETS; socket+=4) {
            dramPerSocket[socket] = diffArray[socket+0];
            pp0PerSocket[socket]  = diffArray[socket+1];
            pp1PerSocket[socket]  = diffArray[socket+2];
            pkgPerSocket[socket]  = diffArray[socket+3];
        }
		this.startTimestamp = startTimestamp;
		this.stopTimestamp = stopTimestamp;
	}

    public double getDRAM(int socket) { return dramPerSocket[socket]; }
    public double getPP0(int socket)  { return pp0PerSocket[socket];  }
    public double getPP1(int socket)  { return pp1PerSocket[socket];  }
    public double getPKG(int socket)  { return pkgPerSocket[socket];  }

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
        return csv + getStartTimestamp() + delim + getTimeElapsed();
	}

    private double raplSubtract(double x, double y) {
        if (x < 0 || y < 0) return -1;
        double diff = x - y;
    }

	public static EnergyMeasurement between(EnergyStats before, EnergyStats after) {
        // see src/native/JNI/EnergyCheckUtils.c for the source of truth behind these indices
        int dram_pos =  0;
        int pp0_pos  =  1;
        int pp1_pos  =  2;
        int pkg_pos  =  3;

        double[] dram = int[ArchSpec.NUM_SOCKETS];
        double[] pp0  = int[ArchSpec.NUM_SOCKETS];
        double[] pp1  = int[ArchSpec.NUM_SOCKETS];
        double[] pkg  = int[ArchSpec.NUM_SOCKETS];
       
        double[] raplCountersBefore = before.getRaplCounters();
        double[] raplCountersAfter  = after.getRaplCoutners();
        for(int socket = 0; socket < ArchSpec.NUM_SOCKETS; ++socket) {
            dram[socket+dram_i] = raplCountersAfter[socket+dram_i] - raplCountersBefore[socket+dram_i];
            pp0[socket+pp0_i]   = raplCountersAfter[socket+pp0_i]  - raplCountersBefore[socket+pp0_i];
            pp1[socket+pp1_i]   = raplCountersAfter[socket+pp1_i]  - raplCountersBefore[socket+pp1_i];
            pkg[socket+pkg_i]   = raplCountersAfter[socket+pkg_i]  - raplCountersBefore[socket+pkg_i];
            if (dram[socket+dram_pos] < 0) {
                
            }
        }

		double[] primitiveSample =
			EnergyMonitor
				.subtractPrimitiveSamples (
					after.getPrimitiveSample(),
					before.getPrimitiveSample()
				);
		return new EnergyMeasurement (
			primitiveSample,
			before.getTimestamp(),
			after.getTimestamp()
		);
	}

}
