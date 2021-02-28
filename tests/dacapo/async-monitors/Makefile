
harness = AsyncMonitorHarness
tmp-dir = tmp-building-jar
dacapo-jar = dacapo-evaluation-git+309e1fa.jar
dacapo-jar-new = dacapo-mytest.jar

# see how to bake your file into the jar (consider just adding it instead of zipping and unzipping)
# https://github.com/dacapobench/dacapobench/blob/master/benchmarks/harness/src/DelayCallback.java

all:
	cp ~/jRAPL/java/target/jRAPL-1.0.jar .
	javac -cp $(dacapo-jar):jRAPL-1.0.jar $(harness).java
	mkdir $(tmp-dir)
	( \
	cd $(tmp-dir) \
	&& jar -xf ../$(dacapo-jar) \
	&& cp ../$(harness).class . \
	&& jar cfm ../$(dacapo-jar-new) META-INF/MANIFEST.MF .\
	)
	rm -r $(tmp-dir)

clean:
	rm -rf $(harness).class $(dacapo-jar-new) $(tmp-dir)
