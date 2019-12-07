TARGET = libCPUScaler.so CPUScaler.o arch_spec.o msr.o dvfs.o
CFLAGS = -fPIC -g -c -Wall
JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"
JAVA_INCLUDE = $(JAVA_HOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

all: lib_shared_CPUScaler

common:
	echo $(JAVA_HOME)
	gcc $(CFLAGS) -I $(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) CPUScaler.c arch_spec.c msr.c dvfs.c

lib_shared_CPUScaler: common
	gcc -g -Wall -I $(JAVA_INCLUDE) -I $(JAVA_INCLUDE_LINUX) -shared -Wl,-soname,libCPUScaler.so -o libCPUScaler.so CPUScaler.o arch_spec.o msr.o dvfs.o -lc

cdriver: *.c
	#gcc -g -fsanitize=address -fno-omit-frame-pointer -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) CPUScaler.c arch_spec.c msr.c dvfs.c cdriver.c -o cdriver -lm
	gcc -g -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) CPUScaler.c arch_spec.c msr.c dvfs.c cdriver.c -o cdriver -lm

clean:
	rm -f lib_share_CPUScaler $(TARGET) cdriver *.class
