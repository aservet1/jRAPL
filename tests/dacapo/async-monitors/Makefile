javafiles = AsyncMonitorCallback.java AsyncMemoryMonitor.java

classfiles = $(javafiles:.java=.class)

dacapo-jar = dacapo-evaluation-git+309e1fa.jar
jRAPL-jar = jRAPL-1.0.jar
classpath = $(dacapo-jar):$(jRAPL-jar):.

all:
	ln -s ../../../src/java/target/$(jRAPL-jar) .
	ln -s ../$(dacapo-jar) .
	javac -cp $(classpath) $(javafiles)

clean:
	rm -f $(classfiles) $(jRAPL-jar) $(dacapo-jar)
