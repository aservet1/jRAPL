#!/bin/bash

if [ -z $1 ]
then
	echo "usage: ./$0 (name to tag onto result files)"
	exit 1
fi

sudo modprobe msr

trials=4000

outputdir=RuntimeResults_$1/PerSocketMSRReadings

rm -rf $outputdir
mkdir -p $outputdir

cd ../src

# we're assuming that this is set up for C side timing of each core reading
sudo java jrapl.RuntimeTestUtils --time-msr-readings $trials > ../RunTimeTests/$outputdir/MajorOutput.temp-data

cd ../RunTimeTests/$outputdir

socket_num=$(../../DataCollectionScripts/get_socket_num)

for (( n=0; n<$socket_num; n++ ))
do
	socket="Socket"$n
	mkdir $socket
	cd $socket
	overall_output=$socket.overall-data
	grep $socket ../MajorOutput.temp-data > $overall_output
	#cat $overall_output
	#python3 ../../../DataCollectionScripts/cleanup_data.py

	cd ..
done

rm MajorOutput.temp-data
