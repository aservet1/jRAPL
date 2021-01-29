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
import java.util.concurrent.TimeUnit;


public class NativeMethods {

	@State(Scope.Thread)
    public static class MyState {
		public EnergyManager manager;

        @Setup(Level.Trial)
        public void doSetup() {
            manager = new EnergyManager();
			manager.init();
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
			manager.dealloc();
        }
    }

	@State(Scope.Thread)
	public static class ProfileDeallocState {

		@Setup(Level.Trial)
		public void doSetup(){
			new EnergyManager().init();
		}
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 1)
	@Measurement(iterations = 1)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeProfileDealloc(ProfileDeallocState s) {
		EnergyManager.profileDealloc();
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 1)
	@Measurement(iterations = 1)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeProfileInit() throws InterruptedException {
		EnergyManager.profileInit();
		TimeUnit.MILLISECONDS.sleep(1); // repeatedly accessing MSRs without break eventually shuts them down and causes register read error
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 0)
	@Measurement(iterations = 1)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeEnergyStatCheck(Blackhole b, MyState state) throws InterruptedException {
		b.consume(EnergyMonitor.energyStatCheck(0));
		//TimeUnit.MILLISECONDS.sleep(1); // repeatedly accessing MSRs without break eventually shuts them down and causes register read error
	}

}
