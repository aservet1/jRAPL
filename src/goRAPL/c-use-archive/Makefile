
TARGET = energy_stat_check

NATIVE_FILES_LOCATION = ../../native/
NATIVE_LIB = $(NATIVE_FILES_LOCATION)/libNativeRAPL.a

INCLUDE_FLAGS = -I$(NATIVE_FILES_LOCATION) 

all:
	gcc $(INCLUDE_FLAGS) $(TARGET).c -L$(NATIVE_FILES_LOCATION) -lNativeRAPL -lm -o $(TARGET)
	
clean:
	rm -f $(TARGET)

