javafiles = CalmnessCallback.java AsyncFrequencyMonitor.java

classfiles = $(javafiles:.java=.class)

dacapo-jar = dacapo-evaluation-git+309e1fa.jar
jRAPL-jar = jRAPL-1.0.jar
classpath = $(dacapo-jar):$(jRAPL-jar):.

all:
	[ -f $(jRAPL-jar)  ] || ln -s ../../../src/java/target/$(jRAPL-jar) .
	[ -f $(dacapo-jar) ] || ln -s ../$(dacapo-jar) .
	javac -cp $(classpath) $(javafiles)

clean:
	rm -f $(classfiles) $(jRAPL-jar) $(dacapo-jar)
