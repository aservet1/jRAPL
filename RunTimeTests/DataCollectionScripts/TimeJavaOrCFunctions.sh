#!/bin/bash



sudo modprobe msr

trials=4000


if [ "$#" -lt 2 ]; then
    echo "Usage: $0 $# name [java | c [number of iterations]]" >&2
    exit
fi

if [ "$1" = "c" ]; then
    outputdir=RuntimeResults/CFunctions
    flag="--time-native-calls"
elif [ "$1" = "java" ]; then
    outputdir=RuntimeResults/JavaFunctions
    flag="--time-java-calls"
fi


mkdir -p $outputdir

cd ../src

sudo java jrapl.RuntimeTestUtils $flag $trials > ../RunTimeTests/$outputdir/MajorOutput.data
cd ../RunTimeTests/$outputdir

functions='ProfileInit GetSocketNum EnergyStatCheck ProfileDealloc'


for f in $functions
do
	echo $f.data
	file=$f.data
	grep $f MajorOutput.data > $file
done

rm MajorOutput.data

python3 ../../DataCollectionScripts/cleanup_data.py

python3 ../../DataCollectionScripts/create_graphs.py
