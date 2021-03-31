
JAVAHOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVAHOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

JNI_LIB = libJNIRAPL.so
NATIVE_FILES_LOCATION = ..
NATIVE_LIB = $(NATIVE_FILES_LOCATION)/libNativeRAPL.a

INCLUDE_FLAGS = -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) -I$(NATIVE_FILES_LOCATION) 

JNIEXPORTS = \
		ArchSpec.c \
		AsyncEnergyMonitor.c \
		EnergyCheckUtils.c \
		\
		RuntimeTestUtils.c
OFILES = $(JNIEXPORTS:.c=.o)

COMPILER = clang
CFLAGS = -fPIC -g -c -Wall -O1

all: $(JNI_LIB)
	cp $(JNI_LIB) ../../java/src/main/resources/native # make sure to modularize this variable name

$(JNI_LIB): $(JNIEXPORTS)
	$(COMPILER) $(CFLAGS) $(INCLUDE_FLAGS) $(JNIEXPORTS) $(JNIEXPORTS)
	$(COMPILER) -g -Wall $(INCLUDE_FLAGS) -shared -Wl,-soname,$(JNI_LIB) -o $(JNI_LIB) $(OFILES) -L $(NATIVE_FILES_LOCATION) -lc -lNativeRAPL
clean:
	rm -f $(JNI_LIB) $(OFILES) $(CDRIVER)
	rm -f ../../java/src/main/resources/native/$(JNI_LIB) # make sure to modularize this variable name

