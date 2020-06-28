TARGET = libCPUScaler.so CPUScaler_TimingUtils.o CPUScaler.o arch_spec.o msr.o dvfs.o
CFLAGS = -fPIC -g -c -Wall
JAVAHOME=${JAVA_HOME}
JAVA_INCLUDE = $(JAVAHOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

all: lib_shared_CPUScaler javafiles

common:
	echo $(JAVA_HOME)
	gcc $(CFLAGS) -I $(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) CPUScaler_TimingUtils.c CPUScaler.c arch_spec.c msr.c dvfs.c CPUScalerShared.c

lib_shared_CPUScaler: common
	gcc -g -Wall -I $(JAVA_INCLUDE) -I $(JAVA_INCLUDE_LINUX) -shared -Wl,-soname,libCPUScaler.so -o libCPUScaler.so CPUScaler_TimingUtils.o CPUScaler.o arch_spec.o msr.o dvfs.o CPUScalerShared.o -lc

cdriver: *.c
	#gcc -g -fsanitize=address -fno-omit-frame-pointer -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) CPUScaler.c arch_spec.c msr.c dvfs.c cdriver.c -o cdriver -lm
	gcc -g -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) CPUScaler_TimingUtils.c CPUScalerShared.c CPUScaler.c arch_spec.c msr.c dvfs.c cdriver.c -o cdriver -lm

javafiles: *.java
	javac *.java -d .
	cp libCPUScaler.so jrapl/

user-api: *.java
	javadoc JRAPL.java ArchSpec.java EnergyControlUtils.java EnergyCheckUtils.java EnergyReadingCollector.java -d ../UserAPI

clean:
	rm -f lib_share_CPUScaler $(TARGET) cdriver *.class
	rm -rf jrapl/
