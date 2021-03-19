
ctim = ctimer
jtim = Jtimer

all:
	gcc $(ctim).c -o $(ctim)
	javac $(jtim).java

clean:
	rm -rf $(ctim) $(jtim).class

# JAVAHOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
# JAVA_INCLUDE = $(JAVAHOME)/include
# JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux
# 
# LIB = libctimer.so
# CFILE = ctimer.c
# OFILES = $(CFILE:.c=.o)
# 
# INCLUDE_FLAGS = -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX)
# CFLAGS = -fPIC -g -c -Wall
# 
# all: $(LIB)
# 
# $(LIB): $(CFILE)
# 	gcc $(CFLAGS) $(INCLUDE_FLAGS) $(CFILE)
# 	gcc -g -Wall $(INCLUDE_FLAGS) -shared -Wl,-soname,$(LIB) -o $(LIB) $(OFILES)
# 	rm $(OFILES)
# clean:
# 	rm -f $(LIB) $(CDRIVER)
