
NATIVE_SRC = src/native
JAVA_SRC = src/java

DRIVER_OUTPUT_FILES = \
	AsyncMonitor-C-metainfo.json \
	AsyncMonitor-Java-metainfo.json \
	AsyncMonitor-C.csv \
	AsyncMonitor-Java.csv \

all:
	make nativeLib
	make javaLib

nativeLib: ## Compile native .so
	(cd $(NATIVE_SRC) && make)
	(cd $(NATIVE_SRC)/JNI && make)

javaLib: ## Build java .jar
	(cd $(JAVA_SRC) && mvn clean install)

clean:
	(cd $(NATIVE_SRC) && make clean)
	(cd $(NATIVE_SRC)/JNI && make clean)
	(cd $(JAVA_SRC) && mvn clean)
	rm -f $(DRIVER_OUTPUT_FILES)
