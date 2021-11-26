# jRAPL

## Background
jRAPL is a computer energy monitoring API in Java. RAPL stands for Running Average Power Limiting, an interface that Intel provides to monitor power consumption
and set power limits. jRAPL uses the energy monitoring technology and implements it in Java, abstracting out low-level details about register reading and how to
access the data.

I am developing this library as part of a research project for energy aware programming at my university. The [original](https://github.com/kliu20/jRAPL)
version of jRAPL was made several years ago by one of my team members, used for several projects that were going on at the time. I'm taking his existing
code and expanding on it, adding improvements and making it a standalone general purpose library for the research community to be able to use.

This is still an ongoing development project. As of now (June 10, 2021) the API is mostly done and I'm running benchmarks to measure its performance, to
see where it can be optimized, and for a technical writeup of the new development of jRAPL.

Because this is still under development, I don't have robust documentation on all of the features and how it works. I have the basics listed below for if
anyone wants to use it right now for what it is, but there is still development that needs to be done.

## Performance Tests
I'm currently running several runtime and memory overhead tests on the tool. All of the code for that is in the `tests` directory. That part is still
very ad-hoc and under development. Once it's all done, it'll be organized into a way where all of the experiments can be run and the results can be
reported on any system with little to no prior configuration.

I have some runtime micro-benchmarks done with the [Java MicroBenchmark Harness](https://github.com/openjdk/jmh) and some longer-term benchmarks
to observe memory footprint and some other performance metrics with the [DaCapo Benchmark Suite](http://dacapobench.sourceforge.net/).

## How to build
Right now, it's all made by a shell script at the top level, and then Maven plus a few Makefiles. I'll have a more sophisticated build by the time the project is done.
You can do `./build.sh` and `./build clean`. It triggers other makefiles to build the native library, found in `src/native` and calls Maven to compile the Java
files in `src/java`. The final JAR is in `src/java/target/jRAPL-1.0.jar`, which you can move anywhere you want to include in your project and import the
relevant modules.

## The Interface
Since the project is still under development, these are not final, but they are close to final.

### Prerequeisites
The Intel RAPL interface requires root access, so all jRAPL programs need to be run as root. It is also not turned on by default, so you can turn it on it
with `sudo modprobe msr`. If you get errors such as `ERROR read_msr(): pread error!` it's likely that you're either not running root or that you haven't
used `sudo modprobe msr` since the last time the system booted.

### Energy Samples
How to access the energy samples is described in the below sections. There are two ways to implement a sample: as a Java object and as a Java array.
The array provides a less friendly interface, but has less memory footprint. The object has getter methods as well as a timestamp. Units are Joules.
#### Power Domains
A power domain refers to a hardware component whose energy consumption is reported. This implementation of jRAPL can report on DRAM, Cores, CPU Package,
and GPU. Power domains reported are dependent on your machine.

#### EnergyStats vs EnergyDiff
There are two types of energy sample objects. `EnergyStats` is a running total of energy consumed since the last time the reporting register
overflowed back to 0. `EnergyDiff` is the subtraction of two `EnergyStats` objects. The subtraction in constructing and `EnergyDiff` handles wraparound
logic, in case the first sample was ten pre-overflow and the second sample was taken post-overflow.

`EnergyStats` has an `Instant` timestamp for when the sample was taken and `EnergyDiff` has a `Duration` time value for the time over which this sample
refers. It can also be used to calculate power, since you now have joules and time.

Both of these types inherit from the same type, so the getters for energy values per power domain (described below) are the same.

#### Getting values from the sample
The sample object makes it easy to get values for each power domain. Simple getters, ie `sample.getDram()`, which reports the combined DRAM energy
consumptions for all CPU sockets. You can specify which socket to get the value from with `sample.getDram(2)` and you'll get the value for socket 2.
If a power domain is not supported for your mahine, this method will return `-1`.

To get samples from the primitive array, you need the index that corresponds to the power domain per socket. These are in the `ArchSpec` class, which
has a lot of system-specific constants. To get the value out of the array for a given socket, you can do `sample[ArchSpec.DRAM_ARRAY_INDEX*socketNumber]`. If
the power domain is not supported for your machine, the index constant will be `-1`.

### Types of monitors
The library currently has two types of energy monitors, for synchronous and asynchronous energy monitoring. Both have an `activate()` and `deactivate()`
method that allocate and deallocate memory and resources in native C code. Using them before activated or after deactivated is undefined behavior.

#### Synchronous Energy Monitor
The `jRAPL.SyncEnergyMonitor` class is for taking energy samples during the execution of your main program. Sample code:
```
SyncEnergyMonitor m = new SyncEnergyMonitor();
m.activate();

EnergyStats before = m.getSample();
doWork();
EnergyStats after = m.getSample();

EnergyDiff diff = EnergyDiff.between(before, after);
m.deactivate();
```
You now have the amount of energy your machine consumed over the course of `doWork()` in the `diff` variable.

#### Asynchronous Energy monitor
The `jRAPL.AsyncEnergyMonitor` class is a monitor that takes samples at a set sampling rate in a background thread while your main program does its own thing.
You can extract samples in your main thread with the `getLastKSamples()` method or you can dump results to a file in CSV format with `writeFileCSV()`.
Sample code:
```
AsyncEnergyMonitor m = new AsyncEnergyMonitorJavaSide();
m.activate();
m.setSamplingRate(100);

m.start();
doWork();
m.stop();

m.writeFileCSV('data/monitored-activity.csv');

m.deactivate();
```
This will take energy samples ever 100ms while `doWork()` executes and will then dump the collected data to a CSV file.

The reason I initialized it as a `new AsyncEnergyMonitorJavaSide()` is because we are testing different implementations of the monitor. We also have two
versions where all the computations are done and stored in native code. The API for how to initialize this will be cleaned up once we find what the best 
implementation is.

## Checking if it works on your machine
To see if your computer's architecture is supported, run 
```sudo java -cp src/java/target/jRAPL-1.0.jar jRAPL.Demo ArchSpec```
which will print out a few architecture-specific parameters that jRAPL uses. The relevant output will be something like:
```
MICRO_ARCHITECTURE: 8e
MICRO_ARCHITECTURE_NAME: KABYLAKE
```
If `MICRO_ARCHITECTURE_NAME` says something like `UNDEFINED_ARCHITECTURE`, it won't work on your machine.
Feel free to open a pull request with whatever your output was and I'll update, if possible.

### Contact
Any questions, feel free to email. Alejandro Servetto {aservet1@binghamton.edu}
