#!/bin/bash

#ignoreLock=-Djmh.ignoreLock=true

mvn clean install && ./transferjars.sh ../../../src/java/target/jRAPL-1.0.jar target/benchmarks.jar
[ $? == 0 ] || exit

rm -rf data && mkdir data # where results will be written to
sudo java -jar $ignoreLock target/benchmarks.jar

echo ">> jmh done, now analyzing results"

for x in JavaSide CSide readMSR
do
	./scripts/analyze-and-plot.sh $x
done
