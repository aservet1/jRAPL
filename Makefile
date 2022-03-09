FINAL_JAR = jRAPL.jar

NATIVE_SRC = src/native
JNI_SRC    = src/native/JNI
JAVA_SRC   = src/java

SAMPLE_DRIVER_OUTPUT_FILES = \
	AsyncMonitor.csv         \
	AsyncMonitor-metainfo.json

NATIVE_TARGET = src/JNI/libJNIRAPL.so

all: clean nativeLib javaLib

nativeLib: ## Compile native .so
	(cd $(NATIVE_SRC) && make)
	(cd $(JNI_SRC) && make)

javaLib: ## Build java .jar
	(cd $(JAVA_SRC) && mvn clean install)
	cp $(JAVA_SRC)/target/jRAPL-1.0.jar $(FINAL_JAR)

clean-sample-driver-output:
	rm -f $(SAMPLE_DRIVER_OUTPUT_FILES)

clean: clean-sample-driver-output
	(cd $(NATIVE_SRC) && make clean)
	(cd $(JNI_SRC) && make clean)
	(cd $(JAVA_SRC) && mvn clean)
	rm -f $(FINAL_JAR)
