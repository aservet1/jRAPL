#!/bin/bash

set -e # exits when any command fails

if [ $# != 2 ]; then
	echo "usage: $0 logfile sleepTime"
	exit 2
fi

( cd src/main/resources/myNativeLibrary && make )

ofile=$1
sleepTime=$2

mvn clean install

java -DsleepTime=$sleepTime -jar target/benchmarks.jar | tee $ofile
