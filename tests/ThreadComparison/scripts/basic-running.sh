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

for jc in java c-linkedlist c-dynamicarray
do
	sudo java -cp $JRAPL_HOME/target/jrapl-1.0.jar jrapltesting.ThreadTesting \
		$jc $monitor_lifetime $monitor_delay "output/$jc.data"
	echo "done with $jc collecting"
done

cd output
data_output_files="c-dynamicarray.data c-linkedlist.data java.data"

python3 ../scripts/zero-reading-interval.py	$data_output_files > stats/zero-intervals.stats
echo "done with zero interval"
python3 ../scripts/avg-nonzero-energy-read.py $data_output_files > stats/avg-nonzero-energy-read.stats
echo "done with ener read analysis"

#rm $data_output_files
