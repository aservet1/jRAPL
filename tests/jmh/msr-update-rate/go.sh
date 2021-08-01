#!/bin/bash

sudo -v; set -e

mvn clean install

hostName=$(hostname)
outputDir=results
outputLog=$outputDir/output.log

mkdir -p $outputDir

sudo java \
	-DhostName=$hostName \
	-jar target/benchmarks.jar \
	-rf json \
	| tee $outputLog

sudo mv \
	jmh-result.json \
	$outputDir/last-jmh-output_$hostName.json

