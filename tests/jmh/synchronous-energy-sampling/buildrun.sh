#!/bin/bash

#ignoreLock=-Djmh.ignoreLock=true

sudo -v

mvn clean install && ./transferjars.sh ../../../src/java/target/jRAPL-1.0.jar target/benchmarks.jar
[ $? == 0 ] || exit

rm -rf data && mkdir data # where results will be written to
sudo java -jar $ignoreLock target/benchmarks.jar -rf json
mv jmh-result.json LastResultDone.json
