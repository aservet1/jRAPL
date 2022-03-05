## A pure-C archive library that provides RAPL utilities. Meant to be compiled
## with JNI files into a .so which Java side code will load and use

LIB = libNativeRAPL.a

CFILES = \
		energy_check_utils.c \
		arch_spec.c \
		msr.c \
		async_energy_monitor.c \
		cside_data_storage.c \
		utils.c \

OFILES = $(CFILES:.c=.o)
CDRIVER = cdriver

COMPILER = gcc
CFLAGS = -fPIC -g -c -Wall

all:
	make $(LIB)
	rm -f $(OFILES)

$(LIB): $(CFILES)
	$(COMPILER) $(CFLAGS) $(CFILES)
	ar -rc $(LIB) $(OFILES)

$(CDRIVER): $(LIB) $(CFILES) $(CDRIVER).c
	$(COMPILER) -g -fsanitize=address -fno-omit-frame-pointer $(CDRIVER).c $(LIB) -o $(CDRIVER) -lm -lpthread
	#gcc -g -fsanitize=address -fno-omit-frame-pointer $(CDRIVER).c $(LIB) -o $(CDRIVER) -lm -lpthread

clean:
	rm -f $(LIB) $(OFILES) $(CDRIVER)
