
javafiles = BenchmarkTimer.java

classfiles = $(javafiles:.java=.class)

dacapo-jar = dacapo-evaluation-git+309e1fa.jar
classpath = $(dacapo-jar):.

all:
	javac -cp $(classpath) $(javafiles)

clean:
	rm -f $(classfiles)
