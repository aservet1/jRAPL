#!/bin/bash

JRAPL_HOME="/home/alejandro/jRAPL"
RUNTIME_TESTS="$JRAPL_HOME/tests/RuntimeTests"
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

warmup_iterations=5
for (( i=1; i<=$warmup_iterations; i++ ))
do
	sudo java jrapltesting.RuntimeTestUtils $flag $trials > /dev/null
	echo "$flag: warmup iteration done $i/$warmup_iterations"
done

sudo java jrapltesting.RuntimeTestUtils $flag $trials > $RUNTIME_TESTS/$outputdir/MajorOutput.data
echo "$flag: test done running"

cd $RUNTIME_TESTS/$outputdir

functions='profileInit energyStatCheck profileDealloc' #consider putting 'getSocketNum' back into the list

for f in $functions
do
	file=$f.data
	grep $f MajorOutput.data > $file
done

rm MajorOutput.data

python3 $JRAPL_HOME/tests/RuntimeTests/DataCollectionPrograms/cleanup_data.py
python3 $JRAPL_HOME/tests/RuntimeTests/DataCollectionPrograms/create_graphs.py

echo "$flag: data analysis complete"
