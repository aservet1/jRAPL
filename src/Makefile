TARGET = libCPUScaler.so $(OFILES)

CFILES =	CPUScaler.c			\
		arch_spec.c			\
		msr.c				\
		dvfs.c				\
 		EnergyStats.c 			\
		AsyncEnergyMonitorCSide.c	\
		AsyncEnergyMonitorCSide_JNI.c	\
		RuntimeTestUtils.c		\

OFILES = $(CFILES:.c=.o)

JAVA_TESTFILES = 	RuntimeTestUtils.java	\
			GeneralTestDriver.java	\
			ThreadTesting.java	\
			MemoryTestUtils.java	\

JAVA_USERFILES =	ArchitectureSpecifications.java 		\
			EnergyCheckUtils.java				\
			JRAPL.java					\
			EnergyControlUtils.java				\
									\
			AsyncMonitor.java				\
			AsyncMemoryMonitor.java				\
			AsyncEnergyMonitorCSide.java			\
			AsyncEnergyMonitorJavaSide.java			\
			AsyncEnergyMonitorJavaSide_ArraySamples.java	\
			AsyncEnergyMonitorJavaSide_ObjectSamples.java	\
			AsyncMemoryMonitor.java				\
			NativeUtils.java				\
			PerfCheckUtils.java				\
									\
			EnergyManager.java				\
			AsyncEnergyManager.java				\
			SyncEnergyManager.java				\
									\
			EnergySample.java				\
			EnergyStats.java				\
			EnergyDiff.java					\



CFLAGS = -fPIC -g -c -Wall
JAVA_HOME = /usr/lib/jvm/java-11-openjdk-amd64
JAVA_INCLUDE = $(JAVA_HOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

all: lib_shared_CPUScaler javafiles

common: $(CFILES)
	gcc $(CFLAGS) -I $(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES)
lib_shared_CPUScaler: common
	gcc -g -Wall -I $(JAVA_INCLUDE) -I $(JAVA_INCLUDE_LINUX) -shared -Wl,-soname,libCPUScaler.so -o $(TARGET) -lc

cdriver: $(CFILES) cdriver.c
	#gcc -g -fsanitize=address -fno-omit-frame-pointer -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) CPUScaler.c arch_spec.c msr.c dvfs.c cdriver.c -o cdriver -lm
	gcc -g -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES) cdriver.c -o cdriver -lm -lpthread

javafiles: $(JAVA_TESTFILES) $(JAVA_USERFILES)
	javac $(JAVA_TESTFILES) $(JAVA_USERFILES) -d .

user-api: *.java
	javadoc $(JAVA_USERFILES) -d ../UserAPI

clean:
	rm -f lib_share_CPUScaler $(TARGET) cdriver *.class
	rm -rf jrapl/ jrapltesting
