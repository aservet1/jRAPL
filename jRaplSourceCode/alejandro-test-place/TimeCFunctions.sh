#!/bin/bash

sudo modprobe msr

trials=4000

rm -rf CData
mkdir CData

cd ..
sudo java jrapl.DriverAlejandro $trials > alejandro-test-place/CData/MajorOutput.data
cd alejandro-test-place/CData

functions='EnergyStatCheck GetSocketNum ProfileInit ProfileDealloc'


for f in $functions
do
	echo $f.data
	file=$f.data
	grep $f MajorOutput.data > $file
done

rm MajorOutput.data

for file in $(ls)
do
	python3 ../AvgStDev.py $file >> AVG_STDEV.data
	echo '------------------------------' >> AVG_STDEV.data
done
