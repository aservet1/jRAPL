TARGET = libCPUScaler.so $(OFILES)

CFILES = CPUScaler_TimingUtils.c CPUScaler.c arch_spec.c msr.c dvfs.c
OFILES = $(CFILES:.c=.o) 

CFLAGS = -fPIC -g -c -Wall
JAVAHOME=${JAVA_HOME}
JAVA_INCLUDE = $(JAVAHOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

all: lib_shared_CPUScaler javafiles

common:
	echo $(JAVA_HOME)
	gcc $(CFLAGS) -I $(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES)
lib_shared_CPUScaler: common
	gcc -g -Wall -I $(JAVA_INCLUDE) -I $(JAVA_INCLUDE_LINUX) -shared -Wl,-soname,libCPUScaler.so -o $(TARGET) -lc

cdriver: *.c
	#gcc -g -fsanitize=address -fno-omit-frame-pointer -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) CPUScaler.c arch_spec.c msr.c dvfs.c cdriver.c -o cdriver -lm
	gcc -g -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES) cdriver.c -o cdriver -lm

javafiles: *.java
	javac *.java -d .
	cp libCPUScaler.so jrapl/

user-api: *.java
	javadoc JRAPL.java ArchSpec.java EnergyControlUtils.java EnergyCheckUtils.java EnergyReadingCollector.java -d ../UserAPI

clean:
	rm -f lib_share_CPUScaler $(TARGET) cdriver *.class
	rm -rf jrapl/
