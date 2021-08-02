#!/bin/bash

## Assumes the parameters I usually use, the only thing needed to specify is the output directory

function usage() {
	echo "usage: $1 <result directory> [optional log file]"
	exit 1
}

set -e

[ $# = 0 ] && usage $0

warmups=0 #5
iterations=1 #30
samplingRate=1
resultDir=$1
[ -z $2 ] && logfile=/dev/null || logfile=$2
if [ -d $resultDir ]; then
	echo " ERROR: directory '$resultDir' already exists."
	echo "    please pick another name, or manually delete it (and be sure that you want to delete it AND that it's the one you actually want to delete / not a typo)"
	exit 1
fi
if [ -f $logfile ] && [ $logfile != /dev/null ]; then
	echo "ERROR: logfile '$logfile' already exists!"
	exit 1
fi

touch $logfile

echo " (||(.. Started on $(date)" | tee -a $logfile

sudo -v
sudo echo 'hello w0rld :))' | tee -a $logfile

mkdir $resultDir
sudo ps -ef > $resultDir/SYSTEM_PROCESSES_THAT_WERE_RUNNING.txt

make clean all | tee -a $logfile

for benchmark in $(sed 's/#.*$//g' benchmarks.config)
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
		$warmups no-monitor $samplingRate $resultDir

done 2>&1 | tee -a $logfile

echo " (||(.. Completed on $(date)" | tee -a $logfile

