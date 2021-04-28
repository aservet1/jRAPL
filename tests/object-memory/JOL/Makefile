
jol-jar = jol-cli-0.9-full.jar
jRAPL-jar = jRAPL-1.0.jar
classpath = $(jol-jar):$(jRAPL-jar):.

driver = ObjectMemory

all:
	#cp ~/jRAPL/java/target/$(jRAPL-jar) .
	javac -cp $(classpath) $(driver).java

clean:
	rm -f *.class $(jRAPL-jar)
