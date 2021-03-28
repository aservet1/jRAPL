#!/bin/bash

function usage() {
	echo "usage: $1 <benchmark> <monitoring energy? true|false> <iterations> <warmups> <monitorType> <result directory name>"
	echo "monitorType = java|c-linklist|c-dynamicarray"
	exit 1
}

mycallback=AsyncMonitorCallback
dacapo_jar="dacapo-evaluation-git+309e1fa.jar"
jRAPL_jar="jRAPL-1.0.jar"
classpath="$dacapo_jar:$jRAPL_jar:."

[ $# != 6 ] && usage $0 $@

benchmark=$1
monitoringEnergy=$2
iterations=$3
warmups=$4
monitorType=$5
resultDir=$6

#rm -rf $resultDir && mkdir $resultDir

sudo java -DmonitoringEnergy=$monitoringEnergy -Dwarmups=$warmups \
			-DmonitorType=$monitorType -DresultDir=$resultDir \
			-cp $classpath Harness \
			$benchmark -c $mycallback -n $iterations

