
callback = AsyncMonitorCallback
dacapo-jar = dacapo-evaluation-git+309e1fa.jar
jRAPL-jar = jRAPL-1.0.jar
classpath = $(dacapo-jar):$(jRAPL-jar):.

all:
	cp ~/jRAPL/java/target/$(jRAPL-jar) .
	javac -cp $(classpath) $(callback).java

clean:
	rm -f $(callback).class $(jRAPL-jar)
