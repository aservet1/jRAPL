
JAVAHOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVAHOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

JNI_LIB = libJNIRAPL.so
NATIVE_FILES_LOCATION = ../native
NATIVE_LIB = $(NATIVE_FILES_LOCATION)/libNativeRAPL.a

MAVEN_RESOURCES = ../../src/java/src/main/resources/native
  # where maven will find the native library in the JavaSide compilation

INCLUDE_FLAGS = -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) -I$(NATIVE_FILES_LOCATION) 

JNIEXPORTS = \
		ArchSpec.c \
		AsyncEnergyMonitor.c \
		EnergyCheckUtils.c \
		\
		RuntimeTestUtils.c

OFILES = $(JNIEXPORTS:.c=.o)

CC = gcc
CFLAGS = -fPIC -g -c -Wall

all: $(JNI_LIB)
	cp $(JNI_LIB) $(MAVEN_RESOURCES)

$(JNI_LIB): $(JNIEXPORTS)
	$(CC) $(CFLAGS) $(INCLUDE_FLAGS) $(JNIEXPORTS) $(JNIEXPORTS)
	$(CC) -g -Wall $(INCLUDE_FLAGS) -shared -Wl,-soname,$(JNI_LIB) -o $(JNI_LIB) $(OFILES) -L $(NATIVE_FILES_LOCATION) -lc -lNativeRAPL

clean:
	rm -f $(JNI_LIB) $(OFILES) $(CDRIVER)
	rm -f $(MAVEN_RESOURCES)/$(JNI_LIB)

