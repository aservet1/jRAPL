
JAVAHOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVAHOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

INCLUDE_FLAGS = -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX)

target = nativesleep

all:
	gcc -c $(INCLUDE_FLAGS) $(target).c
	gcc -shared -o $(target).so $(target).o

clean:
	rm -f $(target).o $(target).so
