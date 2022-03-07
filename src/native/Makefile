## A pure-C archive library that provides RAPL utilities. Meant to be compiled
## with JNI files into a .so which Java side code will load and use

LIB = libNativeRAPL.a

PLATFORM_SUPPORT = platform_support

CFILES = \
		energy_check_utils.c \
		arch_spec.c \
		msr.c \
		utils.c \
        $(PLATFORM_SUPPORT).c

OFILES = $(CFILES:.c=.o)
CDRIVER = cdriver

COMPILER = gcc
CFLAGS = -fPIC -g -c -Wall

all: preprocess_code_from_text $(LIB)

preprocess_code_from_text: $(PLATFORM_SUPPORT).c $(PLATFORM_SUPPORT).tsv generate_$(PLATFORM_SUPPORT).awk

$(PLATFORM_SUPPORT).c: $(PLATFORM_SUPPORT).tsv
	awk -f generate_$(PLATFORM_SUPPORT).awk $< > $@

$(LIB): $(CFILES)
	$(COMPILER) $(CFLAGS) $(CFILES)
	ar -rc $(LIB) $(OFILES)
	rm -f $(OFILES)

$(CDRIVER): $(LIB) $(CFILES) $(CDRIVER).c
	$(COMPILER) -g -fsanitize=address -fno-omit-frame-pointer $(CDRIVER).c $(LIB) -o $(CDRIVER) -lm -lpthread

clean:
	rm -f $(LIB) $(OFILES) $(PLATFORM_SUPPORT).c $(CDRIVER)