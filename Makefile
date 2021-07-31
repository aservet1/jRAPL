
NATIVE_SRC = src/native
JNI_SRC = src/JNI
JAVA_SRC = src/java

DRIVER_OUTPUT_FILES = \
	AsyncMonitor-CLINKED_LIST.csv \
	AsyncMonitor-CLINKED_LIST-metainfo.json \
	AsyncMonitor-CDYNAMIC_ARRAY.csv \
	AsyncMonitor-CDYNAMIC_ARRAY-metainfo.json \
	AsyncMonitor-Java.csv \
	AsyncMonitor-Java-metainfo.json \

all:
	make nativeLib
	make javaLib

nativeLib: ## Compile native .so
	(cd $(NATIVE_SRC) && make)
	(cd $(JNI_SRC) && make)

javaLib: ## Build java .jar
	(cd $(JAVA_SRC) && mvn clean install)

clean-driver-output:
	rm -f $(DRIVER_OUTPUT_FILES)

clean: clean-driver-output
	(cd $(NATIVE_SRC) && make clean)
	(cd $(JNI_SRC) && make clean)
	(cd $(JAVA_SRC) && mvn clean)

