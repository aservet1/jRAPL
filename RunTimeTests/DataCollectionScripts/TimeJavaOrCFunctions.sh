#!/bin/bash



sudo modprobe msr




if [ "$#" -ne 3 ]; then
    echo "Usage: $0 $# name java | c iterations" >&2
    exit
fi

if [ "$2" = "c" ]; then
    outputdir=RuntimeResults_$1/CFunctions
    flag="--time-native-calls"
elif [ "$2" = "java" ]; then
    outputdir=RuntimeResults_$1/JavaFunctions
    flag="--time-java-calls"
fi

trials=$3

mkdir -p $outputdir


cd ../src

sudo java jrapl.RuntimeTestUtils $flag $trials > ../RunTimeTests/$outputdir/MajorOutput.data
cd ../RunTimeTests/$outputdir

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
