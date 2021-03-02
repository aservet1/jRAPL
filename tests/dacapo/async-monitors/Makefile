
package = raplCallback
harness = AsyncMonitorHarness
dacapo-jar = dacapo-evaluation-git+309e1fa.jar
jRAPL-jar = jRAPL-1.0.jar

all:
	cp ~/jRAPL/java/target/$(jRAPL-jar) .
	javac -cp $(dacapo-jar):$(jRAPL-jar):. $(package)/$(harness).java
