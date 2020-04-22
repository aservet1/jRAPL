#!/bin/bash

sudo modprobe msr

trials=4000

rm -rf JavaData
mkdir JavaData

cd ../src

sudo java jrapl.RuntimeTestUtils --time-java-calls $trials > ../RunTimeTests/JavaData/MajorOutput.data
cd ../RunTimeTests/JavaData

functions='ProfileInit GetSocketNum EnergyStatCheck ProfileDealloc'


for f in $functions
do
	echo $f.data
	file=$f.data
	grep $f MajorOutput.data > $file
done

rm MajorOutput.data

python3 ../DataCollectionScripts/cleanup_data.py 

python3 ../DataCollectionScripts/create_graphs.py

