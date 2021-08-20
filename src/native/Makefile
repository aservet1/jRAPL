
LIB = libNativeRAPL.a  ## The main target of this makefile: A pure-C archive library that provides RAPL utilities, either on its own or bundled into a JNI library, or other future non-C-language-interface libraries

CFILES = \
		energy_check_utils.c \
		arch_spec.c \
		msr.c \
		async_energy_monitor.c \
		cside_data_storage.c \
		utils.c \
		\
		dvfs.c \

OFILES = $(CFILES:.c=.o)
CDRIVER = cdriver

COMPILER = gcc
CFLAGS = -fPIC -g -c -Wall

all: $(LIB)

$(LIB): $(CFILES)
	$(COMPILER) $(CFLAGS) $(CFILES)
	ar -rc $(LIB) $(OFILES)

$(CDRIVER): $(LIB) $(CFILES) $(CDRIVER).c
	$(COMPILER) -g -fsanitize=address -fno-omit-frame-pointer $(CDRIVER).c $(LIB) -o $(CDRIVER) -lm -lpthread
	#gcc -g -fsanitize=address -fno-omit-frame-pointer $(CDRIVER).c $(LIB) -o $(CDRIVER) -lm -lpthread

clean:
	rm -f $(LIB) $(OFILES) $(CDRIVER)
