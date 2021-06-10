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

public class EnergySampling {

	@State(Scope.Thread)
    public static class MyState {
		public SyncEnergyMonitor monitor;

        @Setup(Level.Trial)
        public void doSetup() {
            monitor = new SyncEnergyMonitor();
			monitor.activate();
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
			monitor.deactivate();
        }
    }

	@Benchmark
	@Warmup(iterations = 7)
	@Fork(1)
	@Measurement(iterations = 30)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeNativeGetSample(MyState state, Blackhole b) {
		b.consume(EnergyMonitor.energyStatCheck());
		Util.busyWait(b);
		
	}

	@Benchmark
	@Warmup(iterations = 7)
	@Fork(1)
	@Measurement(iterations = 30)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeGetSample(MyState state, Blackhole b) {
		b.consume(state.monitor.getSample());
		Util.busyWait(b);
	}
	
	@Benchmark
	@Warmup(iterations = 7)
	@Fork(1)
	@Measurement(iterations = 30)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeGetPrimitiveSample(MyState state, Blackhole b) {
		b.consume(state.monitor.getPrimitiveSample());
		Util.busyWait(b);
	}

}
