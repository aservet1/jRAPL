all:
	make nativeLib
	make javaLib

nativeLib: ## Compile native .so
	(cd native && make)
	(cd native/JNI && make)

javaLib: ## Build java .jar
	(cd java && mvn clean install)

clean:
	(cd native && make clean)
	(cd native/JNI && make clean)
	(cd java && mvn clean)
	rm -f AsyncMonitor-C.tmp AsyncMonitor-Java.tmp
