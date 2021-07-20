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

public class CSideCalls {

	@State(Scope.Thread)
    public static class State_ {
		private HashMap<Long, Long> histogram = new HashMap<Long, Long>();

		protected String dataDir = System.getProperty("dataDir");
		protected String name;

		public void addValue(long microSeconds) {
			if (getIter() >= startIter) { // don't add value if youre still in warmup
				histogram.put(microSeconds, histogram.containsKey(microSeconds) ? histogram.get(microSeconds)+1 : 1);
			}
		}

		protected final int WARMUPS = 5;
		// protected final int WARMUPS = 1;

		private int iterNum = 0;
		private int startIter;
		
		public void incrementIter() {
			this.iterNum += 1;
		}

		public int getIter() {
			return this.iterNum;
		}

		public void setStartIter(int iterNum) {
			this.startIter = iterNum;
		}

		public void doInitialSetup() {
			this.setStartIter(WARMUPS+1);
			EnergyManager.loadNativeLibrary();
			RuntimeTestUtils.initCSideTiming();
		}

		@TearDown(Level.Trial)
		public void doFinalTeardown() {
			RuntimeTestUtils.deallocCSideTiming();
			try {
				FileWriter myHistogramWriter = new FileWriter(dataDir + "/CSide_"+name+"_histogram.data");
				histogram.forEach((k, v) -> {
					try {
						myHistogramWriter.write(Long.toString(k) + " " + Long.toString(v) + System.lineSeparator());
					}
					catch (IOException e) {
						System.out.println("An error occurred.");
						e.printStackTrace();
					}
				});
				myHistogramWriter.flush();
				myHistogramWriter.close();
				System.out.println("Successfully wrote to the file.");
			} catch (IOException e) {
				System.out.println("An error occurred dumping hashmap to file.");
				e.printStackTrace();
			}
		}
	}

	// @State(Scope.Thread)
    // public static class ProfileInitState extends State_ {
	// 	@Setup(Level.Iteration)
	// 	public void incrementIteration() {
	// 		this.incrementIter();
	// 	}

	// 	@Setup(Level.Trial)
	// 	public void doInitalSetup() {
	// 		super.doInitialSetup();
	// 		name = "ProfileInit";
	// 	}

    //     @TearDown(Level.Invocation)
    //     public void doTearDown() throws InterruptedException {
	// 		EnergyManager.profileDealloc();
    //     }

    // }
	// @State(Scope.Thread)
    // public static class EnergyStatCheckState extends State_ {
	// 	@Setup(Level.Iteration)
	// 	public void incrementIteration() {
	// 		this.incrementIter();
	// 	}

	// 	@Setup(Level.Trial)
	// 	public void doInitalSetup() {
	// 		super.doInitialSetup();
	// 		name = "EnergyStatCheck";
	// 	}

    //     @TearDown(Level.Invocation)
    //     public void doTearDown() throws InterruptedException {
	// 		EnergyManager.profileDealloc();
    //     }

    // }
	// @Benchmark
	// @Fork(1)
	// @Warmup(iterations = 5) @Measurement(iterations = 25)
	// // @Warmup(iterations = 1) @Measurement(iterations = 3)
	// @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	// public void timeEnergyStatCheck(EnergyStatCheckState s, Blackhole b) throws InterruptedException {
	// 	s.addValue(RuntimeTestUtils.usecTimeEnergyStatCheck());
	// 	Util.busyWait(b); // repeatedly accessing MSRs without break eventually shuts them down and causes register read error
	// }


	@State(Scope.Thread)
    public static class timecheckerCSideState extends State_ {
		@Setup(Level.Iteration)
		public void incrementIteration() {
			this.incrementIter();
		}

		@Setup(Level.Trial)
		public void doInitalSetup() {
			super.doInitialSetup();
			name = "timecheckerCSide";
		}

        @TearDown(Level.Invocation)
        public void doTearDown() throws InterruptedException {
			EnergyManager.profileDealloc();
        }

    } public static class timecheckerJSideState extends State_ {
		@Setup(Level.Iteration)
		public void incrementIteration() {
			this.incrementIter();
		}

		@Setup(Level.Trial)
		public void doInitalSetup() {
			super.doInitialSetup();
			name = "timecheckerJSide";
		}

        @TearDown(Level.Invocation)
        public void doTearDown() throws InterruptedException {
			EnergyManager.profileDealloc();
        }
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 5) @Measurement(iterations = 50)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timecheckC(timecheckerCSideState s, Blackhole b) throws InterruptedException {
		s.addValue(RuntimeTestUtils.timechecker());
		Util.busyWait(b); // repeatedly accessing MSRs without break eventually shuts them down and causes register read error
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 5) @Measurement(iterations = 50)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timecheckJ(timecheckerJSideState s, Blackhole b) throws InterruptedException {
		RuntimeTestUtils.ctimeStart();
		RuntimeTestUtils.timechecker();
		RuntimeTestUtils.ctimeStop();
		s.addValue(RuntimeTestUtils.ctimeElapsedUsec());
		Util.busyWait(b); // repeatedly accessing MSRs without break eventually shuts them down and causes register read error
	}














	// @State(Scope.Thread)
	// public static class ProfileDeallocState extends State_ {
	// 	@Setup(Level.Iteration)
	// 	public void incrementIteration() {
	// 		this.incrementIter();
	// 	}

	// 	@Setup(Level.Trial)
	// 	public void doInitalSetup() {
	// 		super.doInitialSetup();
	// 		name = "ProfileDealloc";
	// 	}
	// 	@Setup(Level.Invocation)
	// 	public void doSetup(){
	// 		EnergyManager.profileInit();
	// 	}
	// }

	// @Benchmark
	// @Fork(1)
	// // @Warmup(iterations = 5) @Measurement(iterations = 25)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	// @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	// public void timeProfileInit(ProfileInitState pis, Blackhole b) throws InterruptedException {
	// 	pis.addValue(RuntimeTestUtils.usecTimeProfileInit());
	// 	Util.busyWait(b); // repeatedly accessing MSRs without break eventually shuts them down and causes register read error
	// }

	// @Benchmark
	// @Fork(1)
	// // @Warmup(iterations = 5) @Measurement(iterations = 25)
	// @Warmup(iterations = 1) @Measurement(iterations = 3)
	// @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	// public void timeProfileDealloc(ProfileDeallocState pds, Blackhole b) throws InterruptedException {
	// 	pds.addValue(RuntimeTestUtils.usecTimeProfileDealloc());
	// 	Util.busyWait(b); // repeatedly accessing MSRs without break eventually shuts them down and causes register read error
	// }

}
