#!/bin/bash

if [ "$#" -ne 2 ]
then
	echo "usage: $0 [number of trials] [name to tag onto results folder]"
	exit 1
fi

JRAPL_HOME="/home/alejandro/jRAPL"
cd $JRAPL_HOME/tests/RuntimeTests

sudo modprobe msr

trials=$1
outputdir=RuntimeResults_$2/PerSocketMSRReadings

rm -rf $outputdir
mkdir -p $outputdir

cd $JRAPL_HOME/src

sudo java jrapltesting.RuntimeTestUtils --time-msr-readings $trials > $JRAPL_HOME/tests/RuntimeTests/$outputdir/MajorOutput.temp-data

cd $JRAPL_HOME/tests/RuntimeTests/$outputdir

socket_num=$(../../DataCollectionPrograms/get_socket_num)

for (( n=0; n<$socket_num; n++ ))
do
	socket="Socket"$(( n+1 ))
	rm -rf $socket
	mkdir $socket
	cd $socket
	overall_output=$socket.overall-data
	grep $socket ../MajorOutput.temp-data > $overall_output
	
	power_domains='DRAM GPU CORE PKG'

	for p in $power_domains
	do
		file=$p.data
		grep $p $overall_output > $file
	done

	python3 $JRAPL_HOME/tests/RuntimeTests/DataCollectionPrograms/cleanup_data.py
	python3 $JRAPL_HOME/tests/RuntimeTests/DataCollectionPrograms/create_graphs.py

	cd ..
done

rm MajorOutput.temp-data
