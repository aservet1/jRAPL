#!/bin/bash

sudo modprobe msr

trials=40

rm -rf CData
mkdir CData

cd ../jRaplSourceCode
# we're assuming that this is set up for C side timing and the correct java call for that
sudo java jrapl.RuntimeTestUtils --time-native-calls $trials > ../RunTimeTests/CData/MajorOutput.data
cd ../RunTimeTests/CData

functions='ProfileInit GetSocketNum EnergyStatCheck ProfileDealloc'


for f in $functions
do
	echo $f.data
	file=$f.data
	grep $f MajorOutput.data > $file
done

rm MajorOutput.data

for file in $(ls)
do
	python3 ../DataCollectionScripts/AvgStDev.py $file >> c-AVG_STDEV.stats
	python3 ../DataCollectionScripts/create_graphs.py
done
