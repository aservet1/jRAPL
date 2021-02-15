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

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.io.*; 

public class ReadMSR {

	@State(Scope.Thread)
	public static class MyState {

		protected long[] average = new long[ArchSpec.NUM_SOCKETS];
		private int numIterations = 0;

		public void incorporateIntoAverage(long[] trialTime) {
			this.numIterations += 1;
			for (int socket = 1; socket <= ArchSpec.NUM_SOCKETS; socket++) {
				this.average[socket-1] = (
					(this.average[socket-1]*this.numIterations)
					 + trialTime[socket-1]
				) / this.numIterations;
			}
		}
	
		@Setup(Level.Trial)
		public void initialSetup() {
			RuntimeTestUtils.initCSideTiming();
		}

		@TearDown(Level.Trial)
		public void finalTearDown() {
			RuntimeTestUtils.deallocCSideTiming();
		}

	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 1) @Measurement(iterations = 1)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadDRAM(MyState state) {
		state.incorporateIntoAverage(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.DRAM));
	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 1) @Measurement(iterations = 1)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadPKG(MyState state) {
		state.incorporateIntoAverage(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.PKG));
	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 1) @Measurement(iterations = 1)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadGPU(MyState state) {
		state.incorporateIntoAverage(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.GPU));
	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 1) @Measurement(iterations = 1)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeReadCORE(MyState state) {
		state.incorporateIntoAverage(RuntimeTestUtils.usecTimeMSRRead(RuntimeTestUtils.CORE));
	}

}
