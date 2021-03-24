#!/bin/bash

## Assumes the parameters I usually use, the only thing needed to specify is the output directory

function usage() {
	echo "usage: $1 <result directory>"
	exit 1
}

[ $# != 1 ] && usage $0

iterations=15
warmups=5
resultDirBase=$1

rm -rf $resultDirBase ${resultDirBase}_MEMORY && mkdir $resultDirBase ${resultDirBase}_MEMORY

benchmarks=$(sed 's/#.*$//g' all-benchmarks.txt)
#echo $benchmarks
for benchmark in $benchmarks
do
	for monitorType in c-linklist java c-dynamicarray
	do
		for monitoringMemory in 'true' 'false'
		do
			[ $monitoringMemory = 'true' ] && \
				resultDir="${resultDirBase}_MEMORY" || \
				resultDir="${resultDirBase}"

			echo "@@@ Monitoring memory: $monitoringMemory @@@"
			scripts/run-dacapo.sh \
				$benchmark $monitoringMemory $iterations \
				$warmups $monitorType $resultDir
		done
	done
done
