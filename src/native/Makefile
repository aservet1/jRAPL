
LIB = libNativeRAPL.a  ## The main target of this makefile: A pure-C archive library that provides RAPL utilities, either on its own or bundled into a JNI library, or other future non-C-language-interface libraries

CFILES = \
		EnergyCheckUtils.c \
		ArchSpec.c \
		MSR.c \
 		EnergyStats.c \
		AsyncEnergyMonitor.c \
		CSideDataStorage.c \
		Utils.c \
		\
		DVFS.c \

OFILES = $(CFILES:.c=.o)
CDRIVER = cdriver

COMPILER = gcc
CFLAGS = -fPIC -g -c -Wall
JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVA_HOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

all: $(LIB)

$(LIB): $(CFILES)
	$(COMPILER) -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFLAGS) $(CFILES)
	ar -rc $(LIB) $(OFILES)

$(CDRIVER): $(LIB) $(CFILES) $(CDRIVER).c
	$(COMPILER) -g -fsanitize=address -fno-omit-frame-pointer -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CDRIVER).c $(LIB) -o $(CDRIVER) -lm -lpthread
	#gcc -g -fsanitize=address -fno-omit-frame-pointer $(CDRIVER).c $(LIB) -o $(CDRIVER) -lm -lpthread
	#get to a point where you can compile cdriver without depending on setting the 'where is JNI' flag, since the goal of this set of files is to be JNI-independent and just the core C RAPL utilities that can get ported to Java with separate wrapper files, so this core library can in the future be ported to even another language like Go or something

clean:
	rm -f $(LIB) $(OFILES) $(CDRIVER)
