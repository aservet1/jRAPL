TARGET = libCPUScaler.so $(OFILES)

CFILES = EnergyReadingCollector.c CPUScaler_TimingUtils.c CPUScaler.c arch_spec.c msr.c dvfs.c
OFILES = $(CFILES:.c=.o) 

JAVA_TESTFILES = GeneralTestDriver.java RuntimeTestUtils.java ThreadTesting.java
JAVA_USERFILES = ArchSpec.java EnergyCheckUtils.java EnergyControlUtils.java EnergyReadingCollector.java JRAPL.java NativeUtil.java PerfCheckUtils.java


CFLAGS = -fPIC -g -c -Wall
#JAVAHOME=${JAVA_HOME}
JAVAHOME = /usr/lib/jvm/java-11-openjdk-amd64
JAVA_INCLUDE = $(JAVAHOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

all: lib_shared_CPUScaler javafiles

common:
	echo $(JAVA_HOME)
	gcc $(CFLAGS) -I $(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES)
lib_shared_CPUScaler: common
	gcc -g -Wall -I $(JAVA_INCLUDE) -I $(JAVA_INCLUDE_LINUX) -shared -Wl,-soname,libCPUScaler.so -o $(TARGET) -lc

cdriver: $(CFILES) cdriver.c
	#gcc -g -fsanitize=address -fno-omit-frame-pointer -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) CPUScaler.c arch_spec.c msr.c dvfs.c cdriver.c -o cdriver -lm
	gcc -g -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES) cdriver.c -o cdriver -lm -lpthread

javafiles:
	javac *.java -d .
	cp libCPUScaler.so jrapl/
	cp libCPUScaler.so jrapl_testing/

user-api: *.java
	javadoc JRAPL.java ArchSpec.java EnergyControlUtils.java EnergyCheckUtils.java EnergyReadingCollector.java -d ../UserAPI

clean:
	rm -f lib_share_CPUScaler $(TARGET) cdriver *.class
	rm -rf jrapl/ jrapl_testing
