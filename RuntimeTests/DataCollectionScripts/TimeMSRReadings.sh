#!/bin/bash

if [ "$#" -ne 2 ]
then
	echo "usage: ./$0 [number of trials] [name to tag onto results folder]"
	exit 1
fi

sudo modprobe msr

trials=$1

outputdir=RuntimeResults_$2/PerSocketMSRReadings

rm -rf $outputdir
mkdir -p $outputdir

cd ../src

# we're assuming that this is set up for C side timing of each core reading
sudo java jrapl.RuntimeTestUtils --time-msr-readings $trials > ../RuntimeTests/$outputdir/MajorOutput.temp-data

cd ../RuntimeTests/$outputdir

socket_num=$(../../DataCollectionScripts/get_socket_num)

for (( n=0; n<$socket_num; n++ ))
do
	socket="Socket"$n
	mkdir $socket
	cd $socket
	overall_output=$socket.overall-data
	grep $socket ../MajorOutput.temp-data > $overall_output
	
	functions='DRAM CORE PACKAGE'


	for f in $functions
	do
		#echo $f.data
		file=$f.data
		grep $f $overall_output > $file
		#echo $file
		#tail -10 $file
	done

	python3 ../../../DataCollectionScripts/cleanup_data.py
	python3 ../../../DataCollectionScripts/create_graphs.py

	cd ..
done

rm MajorOutput.temp-data
