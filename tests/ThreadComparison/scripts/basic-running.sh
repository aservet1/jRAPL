#!/bin/bash

JRAPL_HOME="/home/alejandro/jRAPL"
cd $JRAPL_HOME/tests/ThreadComparison

if [ "$#" != 2 ]
then
	echo "Usage: $0 <monitor lifetime (ms)> <monitor delay (ms)>"
	exit 1
fi

sudo modprobe msr

sudo rm -rf output
mkdir output
mkdir output/stats

monitor_lifetime=$1
monitor_delay=$2

for jc in java c
do
	sudo java -cp $JRAPL_HOME/src jrapltesting.ThreadTesting \
		$jc $monitor_lifetime $monitor_delay "output/$jc.data"
	echo "done with $jc collecting"
done

cd output

python3 ../scripts/zero-reading-interval.py	c.data java.data > stats/zero-intervals.stats
echo "done with zero interval"
python3 ../scripts/avg-nonzero-energy-read.py	c.data java.data > stats/avg-nonzero-energy-read.stats
echo "done with ener read analysis"
