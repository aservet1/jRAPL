#!/bin/bash

sudo modprobe msr

trials=4000

outputdir=RuntimeResults/JavaFunctions

rm -rf $outputdir
mkdir -p $outputdir

cd ../src

sudo java jrapl.RuntimeTestUtils --time-java-calls $trials > ../RunTimeTests/$outputdir/MajorOutput.data
cd ../RunTimeTests/$outputdir

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

