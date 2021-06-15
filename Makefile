
NATIVE_SRC = src/native
JAVA_SRC = src/java

DRIVER_OUTPUT_FILES = \
	AsyncMonitor-CLINKED_LIST-metainfo.json \
	AsyncMonitor-CLINKED_LIST.csv \
	AsyncMonitor-Java-metainfo.json \
	AsyncMonitor-CDYNAMIC_ARRAY-metainfo.json \
	AsyncMonitor-CDYNAMIC_ARRAY.csv \
	AsyncMonitor-Java.csv \

all:
	make nativeLib
	make javaLib

nativeLib: ## Compile native .so
	(cd $(NATIVE_SRC) && make)
	(cd $(NATIVE_SRC)/JNI && make)

javaLib: ## Build java .jar
	(cd $(JAVA_SRC) && mvn clean install)

clean-driver-output:
	rm -f $(DRIVER_OUTPUT_FILES)

clean:
	(cd $(NATIVE_SRC) && make clean)
	(cd $(NATIVE_SRC)/JNI && make clean)
	(cd $(JAVA_SRC) && mvn clean)
