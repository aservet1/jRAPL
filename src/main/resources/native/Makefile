
NATIVE_LIB = libCPUScaler.so

CFILES =										\
		CPUScaler.c								\
		arch_spec.c								\
		msr.c									\
		dvfs.c									\
 		EnergyStats.c 							\
		AsyncEnergyMonitorCSide.c				\
		AsyncEnergyMonitorCSide_JNIExports.c	\
		RuntimeTestUtils.c						\
		CSideDataStorage.c						\

OFILES = $(CFILES:.c=.o)
TARGET = $(NATIVE_LIB) $(OFILES)
CDRIVER = cdriver
MAKELOG = .makelog

CFLAGS = -fPIC -g -c -Wall
JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVA_HOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

all: lib_shared_CPUScaler
	rm -f $(OFILES)
# remove $(OFILES) because we don't need them after the .so has been created

common: $(CFILES)
	date > $(MAKELOG) #keeps track of last time make occurred
	gcc $(CFLAGS) -I $(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES)

lib_shared_CPUScaler: common
	gcc -g -Wall -I $(JAVA_INCLUDE) -I $(JAVA_INCLUDE_LINUX) -shared -Wl,-soname,$(NATIVE_LIB) -o $(TARGET) -lc

$(CDRIVER): $(CFILES) $(CDRIVER).c
	#gcc -g -fsanitize=address -fno-omit-frame-pointer -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES) $(CDRIVER).c -o $(CDRIVER)-lm
	gcc -g -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES) $(CDRIVER).c -o $(CDRIVER) -lm -lpthread

clean:
	rm -f lib_share_CPUScaler $(TARGET) $(CDRIVER)
	rm -f $(MAKELOG)
