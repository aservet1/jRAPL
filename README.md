# jRAPL

## Background
jRAPL is a computer energy monitoring API in Java. RAPL stands for Running Average Power Limiting, an interface that Intel provides to monitor power consumption
and set power limits. jRAPL uses the energy monitoring technology and implements it in Java, abstracting out low-level details about register reading and how to
access the data.

This is an extension of the [original jRAPL](https://github.com/kliu20/jRAPL). That first version was made by one of the researchers under my advisor
at university. It was used for several PhD projects by different members of the team. As an undergrad, part of my undergraduate research project was to take the 
jRAPL tool and add features, improvements, and modularity to the source code, turning a tool made for a few peoples' projects into a general standalone
energy monitoring tool, available to the open source community for their own energy-aware research and projects.

### Prerequeisites
The Intel RAPL interface requires root access, so all jRAPL programs need to be run as root. The interface is not on by default, it can be
triggered by invoking the Linux kernel module: `sudo modprobe msr`

Maven is required to build the Java part of the library.

Currently supported on Intel processors with Linux OS.

## How to use
Build the jRAPL jar with the Makefile in the root of this project. It will trigger sub-Makefiles and Maven to build and piece together
each component, ending in the jar file. Move/copy this Jar anywhere you'd like and include it in the classpath of any project that references
it. Note that there is a native library in the Jar, so keep this in mind if you're compiling a larger Jar out of this one; you must ensure that
the native library is present in any larger Jar that encompasses this project, otherwise jRAPL can't find the essential native code.

## Checking if it works on your machine
To see if your computer's architecture is supported, run 
```sudo java -cp jRAPL.jar jRAPL.Demo ArchSpec```
which will print out a few architecture-specific parameters that jRAPL uses. The relevant output will be something like:
```
MICRO_ARCHITECTURE: 8e
MICRO_ARCHITECTURE_NAME: KABYLAKE
```
If `MICRO_ARCHITECTURE_NAME` says something like `UNDEFINED_ARCHITECTURE`, it won't work on your machine.
Make an entry in `src/native/platform_support.tsv` to add new platform support. See my "Add New Platform Support section"

### Add new platform suport
If your platform is not currently supported, and you know it has Intel RAPL capabilities, add a new entry in `src/native/platform_support.tsv` and
recompile the whole tool. The build process uses an awk script to generate a C file with the platform support values, which gets compiled in with the
rest of the code.

Sample `platform_support.tsv`:
```
"id"    "platform name" "dram"  "pp0"   "pp1"   "pkg"
0x2A    SANDYBRIDGE     false   true    true     true
0x2D    SANDYBRIDGE_EP  true    true    false    true
0x3A    IVYBRIDGE       false   true    true     true
0x3C    HASWELL1        true    true    false    true
0x3F    HASWELL_EP      true    true    false    true
0x45    HASWELL2        true    true    false    true
0x46    HASWELL3        true    true    false    true
0x4E    SKYLAKE1        true    true    false    true
0x4F    BROADWELL2      true    true    false    true
0x5C    APOLLOLAKE      true    true    false    true
0x5E    SKYLAKE2        true    true    false    true
0x8e    KABYLAKE        true    true    true     true
0x9e    COFFEELAKE2     true    true    false    true
0xD4    BROADWELL       true    true    true     true
```
`id` is the Model ID provided by Intel specific to this architecture. `platform name` can be any unique
string you want that will help you identify it, altough we do recommend that it at least somewhat resembles
the intended name ;)

Simply add a new row with your platform information, available in Intel
documentation.  The rightmost 4 columns indicate whether your platform
supports RAPL for each power domain. It's the updater's responsibility
to know this information and accurately report it.

### Energy Samples
How to access the energy samples is described in the below sections. There are two ways to implement a sample: as a Java object and as a Java array.
The array provides a less friendly interface, but has less memory footprint. The object has getter methods as well as a timestamp. Units are Joules.
#### Power Domains
A "power domain" is a term for how RAPL narrows down energy activity. It refers to a specific hardware component, any RAPL readings will
come from that domain. For example, we have the DRAM domain, so whenever jRAPL returns an energy sample, there will be a DRAM section, which
is energy consumption attributed to your machine's DRAM.

The PKG power domain refers to a CPU package, cores and uncore. The term "socket" (CPU socket, not network socket) is used interchangeably with "Package".

The PP0 power domain refers to all of the CPU cores on a given socket.

The PP1 power domain is platform dependent, but refers to some uncore device, oftentimes an on-chip GPU. It could refer to part of the uncore,
or the entire uncore. `PP0 + PP1 <= PKG`, since PP0 is the energy consumed by CPU cores and PP1 is energy consumed by CPU uncore, or a component
of uncore.

Uncore refers to the parts of a CPU that are not the main cores.

#### EnergySample vs EnergyMeasurement
There are two types of energy sample objects. `EnergySample` is a running total of energy consumed since the last time the reporting register
overflowed back to 0. `EnergyMeasurement` is the subtraction of two `EnergySample` objects. The subtraction in constructing and `EnergyMeasurement`
handles wraparound logic, in case the first sample was ten pre-overflow and the second sample was taken post-overflow.

`EnergySample` has an `Instant` timestamp for when the sample was taken and `EnergyMeasurement` has a start and stop timestamp,
taken from the two `EnergySample` objects that created it. There's a `getElapsedTime()` method to get the duration between these timestamps,
which is most often useful for calculating power; divide the Joules reported by the time period over which your machine consumed
those joules.

The `EnergySample` is intended as only an intermediate value. It's essentially a high level representation of the RAPL MSR counters
converted to Joules, but that's not useful on its own. The way RAPL energy status is reported is as a perpetually accumulating count
of joules consumed so far. The reason this is not useful is because the counters wrap back to zero. This data becomes useful when you
take the measurement of energy consumed between two samples. The logic implemented here takes care of register wraparound and makes sure
that useful, human-consumable values are reported. Therefore, the only useful data you will get is once you convert your series of
`Energy Sample` objects to `Energy Measurement` objects.

#### Getting values from the sample
The sample object makes it easy to get values for each power domain. Simply call one of the EnergyMeasurement.get\*().
`measurement.getDRAM(void)`, which reports the combined DRAM energy consumptions for all CPU sockets. `measurement.getDram(int socket)` and you'll get the value
for the given socket.  If a power domain is not supported for your machine, this will return -1.

### Types of monitors
The library has two types of energy monitors, for synchronous and asynchronous energy monitoring. Both have an `activate()` and `deactivate()`
method that allocate and deallocate memory and other resources in native C code. Using them before activated or after deactivated is undefined behavior.
Failing to call `activate()` before using an energy monitor will cause undefined behaviour and failing to call `deactivate()` will result in
memory leaks and unclosed file descriptors.

#### Synchronous Energy Monitor
The `jRAPL.EnergyMonitor` class is for taking energy samples during the execution of your main program. Sample code:
```
EnergyMonitor m = new EnergyMonitor();
m.activate();

EnergySample before = m.getSample();
doWork();
EnergySample after = m.getSample();

EnergyMeasurement joules = EnergyMeasurement.between(before, after);
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

EnergyMeasurement[] last100 = m.getLastKMeasurements(100); // for if necessary to do online processing of energy, as opposed to profiling and dumping.

m.writeFileCSV('data/monitored-activity.csv');

m.deactivate();
```
This will take energy samples ever 100ms while `doWork()` executes and will then dump the collected data to a CSV file.

### Contact
Any questions, feel free to email. Alejandro Servetto {aservet1@binghamton.edu}
