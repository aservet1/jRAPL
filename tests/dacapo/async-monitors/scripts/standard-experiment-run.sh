#!/bin/bash

## Assumes the parameters I usually use, the only thing needed to specify is the output directory

function usage() {
	echo "usage: $1 <result directory>"
	exit 1
}

[ $# != 1 ] && usage $0

echo " (||(.. Started on $(date)"

sudo -v
sudo echo 'hello w0rld :))'

warmups=5
iterations=30
samplingRate=1
resultDir=$1

if [ -d $resultDir ]; then
	echo " ERROR: directory '$resultDir' already exists."
	echo "    please pick another name, or manually delete it (and be sure that you want to delete it AND that it's the one you actually want to delete / not a typo)"
	exit 1
fi
mkdir $resultDir
sudo ps -ef > $resultDir/SYSTEM_PROCESSES_THAT_WERE_RUNNING.txt

make clean all

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

echo " (||(.. Completed on $(date)"

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
