#!/bin/bash

#ignoreLock=-Djmh.ignoreLock=true

if [ -z $1 ]
then
	echo "usage $0 data_dir"
	exit 2
elif [ -d $1 ]
then
	echo "$1 already exists."
	exit 1
else
	data_dir=$1
	mkdir $data_dir
fi

set -e
sudo -v

mvn clean install && ./scripts/transferjars.sh jRAPL-1.0.jar target/benchmarks.jar

sudo java -DdataDir=$data_dir -jar $ignoreLock target/benchmarks.jar 

echo ">> jmh done, now analyzing results"

for x in JavaSide CSide readMSR
do
	./scripts/analyze-and-plot.sh $x
done
