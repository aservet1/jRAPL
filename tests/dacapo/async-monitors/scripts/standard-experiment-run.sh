#!/bin/bash

## Assumes the parameters I usually use, the only thing needed to spcigy is the output directory


function usage() {
	echo "usage: $1 <result directory rate>"
	echo 1
}

[ $# != 4 ] && usage $0

iterations=15
warmups=5
resultDirBase=$1

for monitoringMemory in 'true' 'false'
do
	[ $monitoringMemory = 'true' ] && resultDir="${resultDirBase}_MEMORY" || resultDir="${resultDirBase}"
	echo "@@@ Monitoring memory: $monitoringMemory @@@"
	scripts/run-every-benchmark-every-type.sh \
		$monitoringMemory $iterations \
		$warmups $monitorType $resultDir
done
