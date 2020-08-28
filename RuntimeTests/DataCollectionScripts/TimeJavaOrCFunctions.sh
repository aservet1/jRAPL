#!/bin/bash

sudo modprobe msr

if [ "$#" != 3 ]; then
    echo "Usage: $0 [java | c] [number of iterations] [name to tag onto results folder]" >&2
    exit 1
fi

if [ "$1" = "c" ]; then
    outputdir=RuntimeResults_$3/CFunctions
    flag="--time-native-calls"
elif [ "$1" = "java" ]; then
    outputdir=RuntimeResults_$3/JavaFunctions
    flag="--time-java-calls"
fi

trials=$2

mkdir -p $outputdir


cd ../src

sudo java jrapltesting.RuntimeTestUtils $flag $trials > ../RuntimeTests/$outputdir/MajorOutput.data
cd ../RuntimeTests/$outputdir


functions='ProfileInit GetSocketNum EnergyStatCheck ProfileDealloc'


for f in $functions
do
	#echo $f.data
	file=$f.data
	grep $f MajorOutput.data > $file
done

rm MajorOutput.data


python3 ../../DataCollectionScripts/cleanup_data.py

python3 ../../DataCollectionScripts/create_graphs.py
