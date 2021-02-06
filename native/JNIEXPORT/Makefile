
JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVA_HOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

NATIVE_FILES_LOCATION = ..

INCLUDE_FLAGS = -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) -I$(NATIVE_FILES_LOCATION) 

JNI_LIB = libJNIRAPL.so
NATIVE_LIB = $(NATIVE_FILES_LOCATION)/libNativeRAPL.a

JNIEXPORTS = \
		ArchSpec.c \
		AsyncEnergyMonitor.c \
		EnergyCheckUtils.c \
		\
		RuntimeTestUtils.c \

OFILES = $(JNIEXPORTS:.c=.o)

CFLAGS = -fPIC -g -c -Wall

all: $(JNI_LIB)
	cp $(JNI_LIB) ../../src/main/resources/native # make sure to modularize this variable name
	javac JavaTest.java -d .

$(JNI_LIB): $(JNIEXPORTS)
	gcc $(CFLAGS) $(INCLUDE_FLAGS) $(JNIEXPORTS) $(JNIEXPORTS)
	gcc -g -Wall $(INCLUDE_FLAGS) -shared -Wl,-soname,$(JNI_LIB) -o $(JNI_LIB) $(OFILES) -L .. -lc -lNativeRAPL
clean:
	rm -f $(JNI_LIB) $(OFILES) $(CDRIVER)
	rm -f ../../src/main/resources/native/$(JNI_LIB) # make sure to modularize this variable name
	rm -f *.class
	rm -r jRAPL

