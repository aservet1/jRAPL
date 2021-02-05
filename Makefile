all:
	make nativeLib
	make javaLib

nativeLib: ## Compile native .so
	(cd native && make)

javaLib: ## Build java .jar
	mvn clean install

clean:
	(cd native && make clean)
	mvn clean
	rm -f AsyncMonitor-C.tmp AsyncMonitor-Java.tmp
