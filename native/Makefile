
NATIVE_LIB = libNativeRAPL.so

CFILES = \
		EnergyCheckUtils.c \
		ArchSpec.c \
		MSR.c \
		DVFS.c \
 		EnergyStats.c \
		AsyncEnergyMonitor.c \
		AsyncEnergyMonitor_JNIExports.c \
		RuntimeTestUtils.c \
		CSideDataStorage.c \

OFILES = $(CFILES:.c=.o)
CDRIVER = cdriver
MAKELOG = .makelog

CFLAGS = -fPIC -g -c -Wall
JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVA_HOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

all: lib_native_RAPL
	cp $(NATIVE_LIB) ../src/main/resources/native
	date > $(MAKELOG) #keeps track of last time 'make' builds the library

lib_native_RAPL: $(CFILES)
	gcc $(CFLAGS) -I $(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES)
	gcc -g -Wall -I $(JAVA_INCLUDE) -I $(JAVA_INCLUDE_LINUX) -shared -Wl,-soname,$(NATIVE_LIB) -o $(NATIVE_LIB) $(OFILES) -lc

$(CDRIVER): $(CFILES) $(CDRIVER).c
	gcc -g -fsanitize=address -fno-omit-frame-pointer -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFILES) $(CDRIVER).c -o $(CDRIVER) -lm -lpthread

clean:
	rm -f $(NATIVE_LIB) $(OFILES) $(CDRIVER) $(MAKELOG)
