#!/bin/bash

function usage() {
	echo "usage: $1 <monitoring memory? true|false> <iterations> <warmups> <result directory name>"
	exit 1
}

mycallback=AsyncMonitorCallback
dacapo_jar="dacapo-evaluation-git+309e1fa.jar"
jRAPL_jar="jRAPL-1.0.jar"
classpath="$dacapo_jar:$jRAPL_jar:."

[ $# != 4 ] && usage $0

monitoringMemory=$1
iterations=$2
warmups=$3
resultDir=$4

#rm -rf $resultDir && mkdir $resultDir

for benchmark in $(cat 'all-benchmarks.txt')
do
	echo "@@@ Doing benchmark $benchmark @@@"

	for monitorType in c-linklist java c-dynamicarray
	do
		scripts/run-dacapo.sh $benchmark $monitoringMemory $iterations $warmups $monitorType $resultDir
		#sudo java -DmonitoringMemory=$monitoringMemory -Dwarmups=$warmups \
		#	-DresultDir=$resultDir -DmonitorType=$type \
		#	-cp $classpath Harness $benchmark -c $mycallback -n $iterations
	done

done
