#!/bin/bash

function usage() {
	echo "usage: $1 <benchmark> <iterations> <result directory name>"
	exit 1
}

mycallback=AsyncMonitorCallback
dacapo_jar="dacapo-evaluation-git+309e1fa.jar"
jRAPL_jar="jRAPL-1.0.jar"
classpath="$dacapo_jar:$jRAPL_jar:."

[ $# != 3 ] && usage $0

benchmark=$1
iterations=$2
resultDir=$3

rm -rf $resultDir && mkdir $resultDir

for type in c-linklist java c-dynamicarray
do
	sudo java -DmonitorType=$type -DresultDir=$resultDir -cp $classpath Harness $benchmark -c $mycallback -n $iterations
done
