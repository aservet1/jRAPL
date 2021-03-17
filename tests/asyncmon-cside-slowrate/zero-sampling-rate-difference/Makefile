
JAVAHOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVAHOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

LIB = libthreadie.so
CFILE = cSide.c
OFILES = $(CFILE:.c=.o)
JAVAFILE = ThreadCompare

INCLUDE_FLAGS = -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX)
CFLAGS = -fPIC -g -c -Wall

all: $(LIB)

$(LIB): $(CFILE)
	gcc $(CFLAGS) $(INCLUDE_FLAGS) $(CFILE)
	gcc -g -Wall $(INCLUDE_FLAGS) -shared -Wl,-soname,$(LIB) -o $(LIB) $(OFILES) -lc -lpthread
	javac $(JAVAFILE).java
clean:
	rm -f $(LIB) $(OFILES) $(CDRIVER) *.class
