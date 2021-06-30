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

package sleeping;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

import java.util.ArrayList;

public class Sleeping {

	public native static void cSleep(int time);
	public native static long cSleepTimed(int time);

	@State(Scope.Thread)
	public static class S {
		private int TIME;
		public ArrayList<Long> cSamples;
		public S() {
			TIME = Integer.parseInt(System.getProperty("sleepTime"));
			cSamples = new ArrayList<>();
			try {
				NativeUtils.loadLibraryFromJar("/myNativeLibrary/nativesleep.so");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(15);
			}
		}
		@TearDown(Level.Trial)
		public void teardown() {
			if (cSamples.size() != 0) {
				long sum = 0;
				for (long t : cSamples) sum += t;
				long average = sum/cSamples.size();
				System.out.println("...> C Timed Average: " + average);
			}
		}
	}

	@Benchmark
	@Fork(1)
	@Warmup(iterations = 2)
	@Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeJSleep(S s) throws InterruptedException {
		Thread.sleep(s.TIME);
	}
	@Benchmark
	@Fork(1)
	@Warmup(iterations = 2)
	@Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeCSleep(S s) throws InterruptedException {
		cSleep(s.TIME);
	}
	@Benchmark
	@Fork(1)
	@Warmup(iterations = 2)
	@Measurement(iterations = 3)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void timeCSleepWithC(S s) throws InterruptedException {
		s.cSamples.add(cSleepTimed(s.TIME));
	}
}
