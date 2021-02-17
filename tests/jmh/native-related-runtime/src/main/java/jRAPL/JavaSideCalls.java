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

import java.util.HashMap;

public class JavaSideCalls {

	@State(Scope.Thread)
	public static class State_ {
		protected HashMap<Long, Long> scatter = new HashMap();
		
		private Instant before;
		private Instant after;

		protected String name;

		public void addValue() {
			if (getIter() >= startIter) {
				long microSeconds = (Duration.between(this.before, this.after).toNanos()) / 1000;
				scatter.put(microSeconds, scatter.containsKey(microSeconds) ? scatter.get(microSeconds)+1 : 1);
			}
		}
		public void setBefore() {
			this.before = Instant.now();
		}
		public void setAfter() {
			this.after = Instant.now();
		}
		

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

		@TearDown(Level.Trial)
		public void doFinalTeardown() {
			try {
				FileWriter myScatterWriter = new FileWriter("JavaSide_"+name+"_scatter.data");
				scatter.forEach((k, v) -> {
					try {
						myScatterWriter.write(Long.toString(k) + " " + Long.toString(v) + System.lineSeparator());
					}
					catch (IOException e) {
						System.out.println("An error occurred.");
						e.printStackTrace();
					}
				});
				myScatterWriter.flush();
				myScatterWriter.close();
				System.out.println("Successfully wrote to the file.");
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}

	}

	@State(Scope.Thread)
    public static class ProfileInitState extends State_ {
		@Setup(Level.Iteration)
		public void incrementIteration() {
			this.incrementIter();
		}

		@Setup(Level.Trial)
		public void doInitialSetup() {
			this.setStartIter(WARMUPS+1);  // CHANGE THIS NUMBER TO BE *num warmup iterations* + 1
			EnergyManager.loadNativeLibrary();
			EnergyManager.profileInit();
			name = "ProfileInit";
		}

        @TearDown(Level.Invocation)
        public void doTearDown() throws InterruptedException {
			EnergyManager.profileDealloc();
        }
  
    }

	@State(Scope.Thread)
	public static class EnergyStatCheckState extends State_ {
		@Setup(Level.Iteration)
		public void incrementIteration() {
			this.incrementIter();
		}

		@Setup(Level.Trial)
		public void initialSetup() {
			this.setStartIter(WARMUPS+1);  // CHANGE THIS NUMBER TO BE *num warmup iterations* + 1
			EnergyManager.loadNativeLibrary();
			EnergyManager.profileInit();
			name = "EnergyStatCheck";
		}
		
	}

	@State(Scope.Thread)
	public static class ProfileDeallocState extends State_ {
		@Setup(Level.Iteration)
		public void incrementIteration() {
			this.incrementIter();
		}

		@Setup(Level.Trial)
		public void doInitialSetup() {
			this.setStartIter(WARMUPS+1);  // CHANGE THIS NUMBER TO BE *num warmup iterations* + 1
			EnergyManager.loadNativeLibrary();
			name = "ProfileDealloc";
		}

		@Setup(Level.Invocation)
		public void doSetup() {
			EnergyManager.profileInit();
		}
		
	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 5) @Measurement(iterations = 10)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeProfileInit(ProfileInitState pis) throws InterruptedException {
		pis.setBefore();
		EnergyManager.profileInit();
		pis.setAfter();
		pis.addValue();
		TimeUnit.MICROSECONDS.sleep(1); // repeatedly accessing MSRs without break eventually shuts them down and causes register read error
	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 5) @Measurement(iterations = 10)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeProfileDealloc(ProfileDeallocState pds) throws InterruptedException{
		pds.setBefore();
		EnergyManager.profileDealloc();
		pds.setAfter();
		pds.addValue();
		TimeUnit.MICROSECONDS.sleep(1); // repeatedly accessing MSRs without break eventually shuts them down and causes register read error
	}

	@Benchmark
	@Fork(1) @Warmup(iterations = 5) @Measurement(iterations = 10)
	@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeEnergyStatCheck(Blackhole b, EnergyStatCheckState escs) throws InterruptedException {
		escs.setBefore();
		b.consume(EnergyMonitor.energyStatCheck());
		escs.setAfter();
		escs.addValue();
		TimeUnit.MICROSECONDS.sleep(1); // repeatedly accessing MSRs without break eventually shuts them down and causes register read error
	}

}
