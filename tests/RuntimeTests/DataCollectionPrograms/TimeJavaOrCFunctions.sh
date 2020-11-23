#!/bin/bash

JRAPL_HOME="/home/alejandro/jRAPL"
cd $JRAPL_HOME/tests/RuntimeTests

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

rm -rf $outputdir
mkdir -p $outputdir

cd $JRAPL_HOME/src

sudo java jrapltesting.RuntimeTestUtils $flag $trials > $JRAPL_HOME/tests/RuntimeTests/$outputdir/MajorOutput.data
cd $JRAPL_HOME/tests/RuntimeTests/$outputdir

functions='profileInit getSocketNum energyStatCheck profileDealloc'

for f in $functions
do
	#echo $f.data
	file=$f.data
	grep $f MajorOutput.data > $file
done

rm MajorOutput.data

python3 $JRAPL_HOME/tests/RuntimeTests/DataCollectionPrograms/cleanup_data.py

python3 $JRAPL_HOME/tests/RuntimeTests/DataCollectionPrograms/create_graphs.py
