#!/bin/bash

sudo modprobe msr

trials=4000

DataCollectDirectory=Socket-MSR-Time-Data

rm -rf $DataCollectDirectory
mkdir $DataCollectDirectory

cd ../jRaplSourceCode

# we're assuming that this is set up for C side timing of each core reading
sudo java jrapl.DriverAlejandro $trials > ../TimingFunctionCalls/$DataCollectDirectory/MajorOutput.temp-data

cd ../TimingFunctionCalls/$DataCollectDirectory

socket_num=$(../get_socket_num)

for (( n=0; n<$socket_num; n++ ))
do
	socket=Socket$n
	mkdir $socket
	cd $socket
	overall_output=$socket.overall-data
	grep $socket ../MajorOutput.temp-data > $overall_output

	power_domains="DRAM GPU PACKAGE CORE"
	for pd in $power_domains
	do
		grep $pd $overall_output | sed 's/^.*: //'> $pd.data
	done

	cd ..
done

rm MajorOutput.temp-data
