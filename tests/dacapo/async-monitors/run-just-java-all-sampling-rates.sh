#!/bin/bash

## Assumes the parameters I usually use, the only thing needed to specify is the output directory

function usage() {
	echo "usage: $1 <result directory> [optional log file]"
	exit 1
}

set -e

[ $# = 0 ] && usage $0

warmups=5
iterations=30
resultDir=$1
[ -z $2 ] && logfile=/dev/null || logfile=$2

rm -f $logfile
touch $logfile

echo " (||(.. Started on $(date)" | tee -a $logfile

sudo -v
sudo echo 'hello w0rld :))' | tee -a $logfile

sudo ps -ef > $resultDir/SYSTEM_PROCESSES_THAT_WERE_RUNNING.txt

make clean all | tee -a $logfile

for samplingRate in 1 2 4 8; do
	for benchmark in $(sed 's/#.*$//g' benchmarks.config); do
		monitoringEnergy=true; monitorType=java
		sudo scripts/run-dacapo.sh \
			$benchmark $monitoringEnergy $iterations \
			$warmups $monitorType $samplingRate $resultDir/samplingrate_$samplingRate/
	done
	printf 'Subject: Your jRAPL experiments are done for sampling rate %d.\nYou will get an email when all of these are done\n' $samplingRate $(hostname) | sudo ssmtp a.l.servetto@gmail.com
done 2>&1 | tee -a $logfile

echo " (||(.. Completed on $(date)" | tee -a $logfile

printf 'Subject: Your jRAPL experiments are done.\nYou can log on to %s to collect your data\n' $(hostname) | sudo ssmtp a.l.servetto@gmail.com

