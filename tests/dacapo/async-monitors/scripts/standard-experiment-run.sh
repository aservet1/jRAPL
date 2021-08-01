#!/bin/bash

## Assumes the parameters I usually use, the only thing needed to specify is the output directory

function usage() {
	echo "usage: $1 <result directory>"
	exit 1
}
[ $# != 1 ] && usage $0

sudo -v
sudo echo 'hello w0rld :))'

make clean all

warmups=5
iterations=30
samplingRate=1
resultDir=$1

rm -rf $resultDir && mkdir $resultDir

benchmarks=$(sed 's/#.*$//g' benchmarks.config)
for benchmark in $benchmarks
do
	monitoringEnergy=true
	for monitorType in c-dynamicarray c-linklist java
	do
		sudo scripts/run-dacapo.sh \
			$benchmark $monitoringEnergy $iterations \
			$warmups $monitorType $samplingRate $resultDir
	done
	monitoringEnergy=false
	sudo scripts/run-dacapo.sh \
		$benchmark $monitoringEnergy $iterations \
		$warmups no-monitor $resultDir
	
done

# benchmarks=$(sed 's/#.*$//g' benchmarks.txt)
# for benchmark in $benchmarks
# do
# 	for monitorType in c-linklist java c-dynamicarray
# 	do
# 		for monitoringEnergy in 'true' 'false'
# 		do
# 			scripts/run-dacapo.sh \
# 				$benchmark $monitoringEnergy $iterations \
# 				$warmups $monitorType $resultDir
# 		done
# 	done
# done
