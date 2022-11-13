#!/bin/bash

## Assumes the parameters I usually use, the only thing needed to specify is the output directory

set -e
sudo -v

warmups=5
iterations=30
resultDir=/media/alejandro/flashdrive/justjava

echo " (||(.. Started on $(date)"

sudo echo 'hello w0rld :))'

ps -ef > SYSTEM_PROCESSES_THAT_WERE_RUNNING.txt

make clean all

for samplingRate in 1 2 4 8; do
	for benchmark in $(sed 's/#.*$//g' benchmarks.config); do
		monitoringEnergy=true; monitorType=java
		sudo runscripts/run-dacapo.sh $benchmark $monitoringEnergy $iterations $warmups $monitorType $samplingRate $resultDir/samplingrate_$samplingRate/
	done
done

echo " (||(.. Completed on $(date)"
