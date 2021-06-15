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
import java.io.*; 

public class ReadMSR {

	@State(Scope.Thread)
	public static class MyState {

		protected String NAME;

		private HashMap<Long, Long> histogram = new HashMap<>();


		protected final int WARMUPS = 5;

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


		public void addValue(long[] runtimePerSocket) {
			if (getIter() >= startIter) {
				for (int socket = 1; socket <= ArchSpec.NUM_SOCKETS; socket++) {
					long microSeconds = runtimePerSocket[socket-1];
					histogram.put(microSeconds, histogram.containsKey(microSeconds) ? histogram.get(microSeconds)+1 : 1);
				}
			}
		}
	
		// @Setup(Level.Trial)
		public void initialSetup() {
			this.setStartIter(WARMUPS+1);  // CHANGE THIS NUMBER TO BE *num warmup iterations* + 1
			EnergyManager.loadNativeLibrary();
			RuntimeTestUtils.initCSideTiming();
		}

		@TearDown(Level.Trial)
		public void finalTearDown() {
			RuntimeTestUtils.deallocCSideTiming();
			try {
				System.out.println("Successfully wrote to the file.");
				FileWriter myHistogramWriter = new FileWriter("readMSR_"+NAME+"_histogram.data");
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
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}

	}

	public static class StateDRAM extends MyState { 
		@Setup(Level.Iteration)
		public void incrementIteration() {
			this.incrementIter();
		}
		@Setup(Level.Trial)
		public void setName() { super.initialSetup(); NAME = "DRAM"; }
	}
	public static class StateGPU extends MyState {
		@Setup(Level.Iteration)
		public void incrementIteration() {
			this.incrementIter();
		}
		@Setup(Level.Trial)
		public void setName() { super.initialSetup(); NAME = "GPU"; }
	}
	public static class StateCORE extends MyState {
		@Setup(Level.Iteration)
		public void incrementIteration() {
			this.incrementIter();
		}
		@Setup(Level.Trial)
		public void setName() { super.initialSetup(); NAME = "CORE"; }
	}
	public static class StatePKG extends MyState {
		@Setup(Level.Iteration)
		public void incrementIteration() {
			this.incrementIter();
		}
		@Setup(Level.Trial)
		public void setName() { super.initialSetup(); NAME = "PKG"; }
	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 5) @Measurement(iterations = 10)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadDRAM(StateDRAM state, Blackhole b) {
		state.addValue(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.DRAM));
		Util.busyWait(b);
	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 5) @Measurement(iterations = 10)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadPKG(StatePKG state, Blackhole b) {
		state.addValue(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.PKG));
		Util.busyWait(b);
	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 5) @Measurement(iterations = 10)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadGPU(StateGPU state, Blackhole b) {
		state.addValue(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.GPU));
		Util.busyWait(b);
	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 5) @Measurement(iterations = 10)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadCORE(StateCORE state, Blackhole b) {
		state.addValue(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.CORE));
		Util.busyWait(b);
	}

}
