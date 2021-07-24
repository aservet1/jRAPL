/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
*/ 

package jRAPL;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashMap;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.time.Duration;

import java.io.FileWriter;
import java.io.IOException;

public class Consolidated {

	@State(Scope.Thread)
	public static class TheState {
		protected HashMap<Long, Long> histogram = new HashMap<Long, Long>();
		
		protected String benchmarkName;
        protected String benchmarkDomain;
		protected String dataDir = System.getProperty("dataDir");

		protected final int WARMUPS = 5;
		// protected final int WARMUPS = 1;

		private int iterNum = 0;
		private int startIter = WARMUPS+1;

		static { // I hope that the static block is a reliable way of doing this in JMH
			EnergyManager.loadNativeLibrary();
		}

		@TearDown(Level.Trial)
		public void doFinalTeardown() {
			try {
				FileWriter myHistogramWriter = new FileWriter (
                    String.format (
                        "%s/%s_%s_histogram.data",
                        dataDir,
                        benchmarkDomain,
                        benchmarkName
                    )
				);
				histogram.forEach((k, v) -> {
					try {
						myHistogramWriter.write(String.format("%d %d\n", k, v));
					}
					catch (IOException e) {
						System.out.println("An error occurred.");
						e.printStackTrace();
					}
				});
				myHistogramWriter.flush();
				myHistogramWriter.close();
				System.out.println("Successfully wrote to the file");
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
			EnergyManager.profileDealloc();
			histogram.clear();
		}

		public void addValue(long[] microSeconds) {
			for (long u : microSeconds) addValue(u);
		}

        public void addValue(long microSeconds) {
			if (!isWarmup()) {
				histogram.put (
                    microSeconds, 
                    (histogram.containsKey(microSeconds))
                    ? histogram.get(microSeconds)+1 
                    : 1
                );
			}
		}
        private boolean isWarmup() {
			return iterNum < startIter;
		}

		@TearDown(Level.Iteration)
		public void incrementIteration() {
			this.iterNum++;
		}

	}

	@State(Scope.Thread)
	public static class JavaSideEnergyStatCheckState extends TheState {
		@Setup(Level.Trial)
		public void initialSetup() {
			EnergyManager.profileInit();
			benchmarkName = "EnergyStatCheck";
            benchmarkDomain = "JavaSide";
		}
	}
	
	@State(Scope.Thread)
    public static class   CSideEnergyStatCheckState  extends TheState {
		@Setup(Level.Trial)
		public void doInitalSetup() {
			RuntimeTestUtils.initCSideTiming();
			benchmarkName = "EnergyStatCheck";
            benchmarkDomain = "CSide";
		}
    }

    @State(Scope.Thread)
	public static class EnergyStatCheckNoReturnState extends TheState {
		@Setup(Level.Trial)
		public void initialSetup() {
			EnergyManager.profileInit();
			benchmarkName = "EnergyStatCheckNoReturnValue";
            benchmarkDomain = "JavaSide";
		}
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 5) @Measurement(iterations = 25)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void cSideTimeEnergyStatCheck(CSideEnergyStatCheckState s, Blackhole b) {
		s.addValue(RuntimeTestUtils.usecTimeEnergyStatCheck());
		Util.busyWait(b);
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 5) @Measurement(iterations = 25)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void javaSideTimeEnergyStatCheck(Blackhole b, JavaSideEnergyStatCheckState s) {
		RuntimeTestUtils.ctimeStart();
		String energy = EnergyMonitor.energyStatCheck();
		RuntimeTestUtils.ctimeStop();
		s.addValue(RuntimeTestUtils.ctimeElapsedUsec());
		b.consume(energy);
		Util.busyWait(b);
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 5) @Measurement(iterations = 25)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeEnergyStatCheckNoReturnValue(Blackhole b, EnergyStatCheckNoReturnState s) {
		RuntimeTestUtils.ctimeStart();
		RuntimeTestUtils.energyStatCheckNoReturnValue();
		RuntimeTestUtils.ctimeStop();
		s.addValue(RuntimeTestUtils.ctimeElapsedUsec());
		Util.busyWait(b);
    }

	// 	// ------------------------------------------------------------------------------------------------------------------------------ //

    @State(Scope.Thread)
	public static class EnergyStatCheckPureCState extends TheState {
		@Setup(Level.Trial)
		public void initialSetup() {
			EnergyManager.profileInit();
			benchmarkName = "EnergyStatCheckPureC";
            benchmarkDomain = "CSide";
		}
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 5) @Measurement(iterations = 25)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeEnergyStatCheckPureC(Blackhole b, EnergyStatCheckPureCState s) {
		long usec = RuntimeTestUtils.energyStatCheckPureC();
		s.addValue(usec);
		Util.busyWait(b);
    }

	// 	// ------------------------------------------------------------------------------------------------------------------------------ //

	public static class StateDRAM extends TheState { 
		// @Setup(Level.Iteration)
		// public void incrementIteration() {
		// 	this.incrementIter();
		// }
		@Setup(Level.Trial)
		public void setName() {
			// super.initialSetup();
			RuntimeTestUtils.initCSideTiming();
			benchmarkName = "DRAM";
			benchmarkDomain = "readMSR";
		}
	}
	public static class StateGPU extends TheState {
		// @Setup(Level.Iteration)
		// public void incrementIteration() {
		// 	this.incrementIter();
		// }
		@Setup(Level.Trial)
		public void setName() {
			// super.initialSetup();
			RuntimeTestUtils.initCSideTiming();
			benchmarkName = "GPU";
			benchmarkDomain = "readMSR";
		}
	}
	public static class StateCORE extends TheState {
		// @Setup(Level.Iteration)
		// public void incrementIteration() {
		// 	this.incrementIter();
		// }
		@Setup(Level.Trial)
		public void setName() {
			// super.initialSetup();
			RuntimeTestUtils.initCSideTiming();
			benchmarkName = "CORE";
			benchmarkDomain = "readMSR";
		}
	}
	public static class StatePKG extends TheState {
		// @Setup(Level.Iteration)
		// public void incrementIteration() {
		// 	this.incrementIter();
		// }
		@Setup(Level.Trial)
		public void setName() {
			// super.initialSetup();
			RuntimeTestUtils.initCSideTiming();
			benchmarkName = "PKG";
			benchmarkDomain = "readMSR";
		}
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 5) @Measurement(iterations = 25)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadDRAM(StateDRAM state, Blackhole b) {
		state.addValue(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.DRAM));
		Util.busyWait(b);
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 5) @Measurement(iterations = 25)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadPKG(StatePKG state, Blackhole b) {
		state.addValue(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.PKG));
		Util.busyWait(b);
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 5) @Measurement(iterations = 25)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadGPU(StateGPU state, Blackhole b) {
		state.addValue(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.GPU));
		Util.busyWait(b);
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 5) @Measurement(iterations = 25)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadCORE(StateCORE state, Blackhole b) {
		state.addValue(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.CORE));
		Util.busyWait(b);
	}

}
