#!/bin/bash

sudo modprobe msr

trials=4000

outputdir=RuntimeResults/PerSocketMSRReadings

rm -rf $outputdir
mkdir -p $outputdir

cd ../src

# we're assuming that this is set up for C side timing of each core reading
sudo java jrapl.RuntimeTestUtils --time-msr-readings $trials > ../RunTimeTests/$outputdir/MajorOutput.temp-data

cd ../RunTimeTests/$outputdir

socket_num=$(../DataCollectionScripts/get_socket_num)

for (( n=0; n<$socket_num; n++ ))
do
	socket="Socket"$n
	mkdir $socket
	cd $socket
	overall_output=$socket.overall-data
	grep $socket ../MajorOutput.temp-data > $overall_output
	#cat $overall_output
	power_domains="DRAM GPU PACKAGE CORE"
	for pd in $power_domains
	do
		grep $pd $overall_output | sed 's/^.*: //'> $pd.data
	done

	cd ..
done

rm MajorOutput.temp-data
