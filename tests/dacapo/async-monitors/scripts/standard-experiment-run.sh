#!/bin/bash

## Assumes the parameters I usually use, the only thing needed to specify is the output directory

function usage() {
	echo "usage: $1 <result directory>"
	exit 1
}

[ $# != 1 ] && usage $0

make

iterations=15
warmups=5
resultDir=$1

#rm -rf $resultDirBase ${resultDirBase}_MEMORY && mkdir $resultDirBase ${resultDirBase}_MEMORY
rm -rf $resultDir && mkdir $resultDir

benchmarks=$(sed 's/#.*$//g' all-benchmarks.txt)
for benchmark in $benchmarks
do
	for monitorType in c-linklist java c-dynamicarray
	do
		for monitoringEnergy in 'true' 'false'
		do
			#[ $monitoringEnergy = 'true' ] && \
			#	resultDir="${resultDirBase}_MEMORY" || \
			#	resultDir="${resultDirBase}"
			echo "@@@ Monitoring memory: $monitoringEnergy @@@"
			scripts/run-dacapo.sh \
				$benchmark $monitoringEnergy $iterations \
				$warmups $monitorType $resultDir
		done
	done
done
