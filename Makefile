NATIVE_SRC = src/native
JNI_SRC    = src/native/JNI
JAVA_SRC   = src/java

SAMPLE_DRIVER_OUTPUT_FILES =                    \
	AsyncMonitor-CLINKED_LIST.csv               \
	AsyncMonitor-CLINKED_LIST-metainfo.json     \
	AsyncMonitor-CDYNAMIC_ARRAY.csv             \
	AsyncMonitor-CDYNAMIC_ARRAY-metainfo.json   \
	AsyncMonitor-Java.csv                       \
	AsyncMonitor-Java-metainfo.json

NATIVE_TARGET = src/JNI/libJNIRAPL.so

all: nativeLib javaLib

nativeLib: ## Compile native .so
	(cd $(NATIVE_SRC) && make)
	(cd $(JNI_SRC) && make)

javaLib: ## Build java .jar
	(cd $(JAVA_SRC) && mvn clean install)

clean-sample-driver-output:
	rm -f $(SAMPLE_DRIVER_OUTPUT_FILES)

clean: clean-sample-driver-output
	(cd $(NATIVE_SRC) && make clean)
	(cd $(JNI_SRC) && make clean)
	(cd $(JAVA_SRC) && mvn clean)
