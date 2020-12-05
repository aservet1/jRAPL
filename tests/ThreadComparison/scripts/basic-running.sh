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

monitor_lifetime=$1
monitor_delay=$2

for jc in java c
do
	sudo java -cp $JRAPL_HOME/src jrapltesting.ThreadTesting \
		$jc $monitor_lifetime $monitor_delay > "output/$jc.data"
	echo "done with $jc collecting"
done

cd output

python3 ../scripts/zero-reading-interval.py	c.data java.data > ../stats/zero-intervals.stats
echo "done with zero interval"
python3 ../scripts/compare-ener-reads.py	c.data java.data > ../stats/compare-ener.stats
echo "done with ener read analysis"
