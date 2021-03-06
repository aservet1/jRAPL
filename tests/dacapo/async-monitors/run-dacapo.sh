#!/bin/bash

function usage() {
	echo "usage: $1 <benchmark> <iterations> <monitorType> <result directory name>"
	echo "monitorType = java|c-linklist|c-dynamicarray"
	exit 1
}

mycallback=AsyncMonitorCallback
dacapo_jar="dacapo-evaluation-git+309e1fa.jar"
jRAPL_jar="jRAPL-1.0.jar"
classpath="$dacapo_jar:$jRAPL_jar:."

[ $# != 4 ] && usage $0

benchmark=$1
iterations=$2
monitorType=$3
resultDir=$4

rm -rf $resultDir && mkdir $resultDir

sudo java -DmonitorType=$monitorType -DresultDir=$resultDir -cp $classpath Harness $benchmark -c $mycallback -n $iterations
