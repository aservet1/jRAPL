#!/bin/bash

sudo modprobe msr

trials=4000

rm -rf CData
mkdir CData

cd ../jRaplSourceCode
# we're assuming that this is set up for C side timing and the correct java call for that
sudo java jrapl.DriverAlejandro $trials > ../TimingFunctionCalls/CData/MajorOutput.data
cd ../TimingFunctionCalls/CData

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
	python3 ../AvgStDev.py $file >> c-AVG_STDEV.data
	echo '------------------------------' >> c-AVG_STDEV.data
done
