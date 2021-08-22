#!/bin/bash

if [ -z $1 ]
then
	echo "usage $0 result_dir"
	exit 2
elif [ -d $1 ]
then
	echo "$1 already exists."
	exit 1
else
	result_dir=$1
fi

set -e; sudo -v

outputlog=$result_dir/output.log

mvn clean install

./scripts/transferjars.sh \
	jRAPL-1.0.jar \
	target/benchmarks.jar

mkdir -p $result_dir
sudo java \
	-DdataDir=$result_dir \
	-jar target/benchmarks.jar \
	| tee $outputlog
