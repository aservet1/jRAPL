
LIB = libNativeRAPL.a

CFILES = \
		EnergyCheckUtils.c \
		ArchSpec.c \
		MSR.c \
 		EnergyStats.c \
		AsyncEnergyMonitor.c \
		CSideDataStorage.c \
		\
		DVFS.c \

OFILES = $(CFILES:.c=.o)
CDRIVER = cdriver
MAKELOG = .makelog

CFLAGS = -fPIC -g -c -Wall
JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVA_HOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

all: $(LIB) JNI

JNI:
	(cd JNIEXPORT && make)

$(LIB): $(CFILES)
	gcc -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CFLAGS) $(CFILES)
	ar -rc $(LIB) $(OFILES)

$(CDRIVER): $(LIB) $(CFILES) $(CDRIVER).c
	gcc -g -fsanitize=address -fno-omit-frame-pointer -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) $(CDRIVER).c $(LIB) -o $(CDRIVER) -lm -lpthread

clean:
	rm -f $(LIB) $(OFILES) $(CDRIVER) $(MAKELOG)
	(cd JNIEXPORT && make clean)
