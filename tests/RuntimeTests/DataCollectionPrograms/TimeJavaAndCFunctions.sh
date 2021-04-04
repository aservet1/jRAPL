#!/bin/bash

JRAPL_HOME="/home/alejandro/jRAPL"
cd $JRAPL_HOME/tests/RuntimeTests

if [ "$#" != 2 ]
then
	echo "Usage $0 [number of trials] [name to tag onto results folder]"
	exit 1
fi

trials=$1
name=$2

./DataCollectionPrograms/TimeJavaOrCFunctions.sh c $trials $name
./DataCollectionPrograms/TimeJavaOrCFunctions.sh java $trials $name

cd RuntimeResults_$2

python3 ../DataCollectionPrograms/bar_graphs.py CFunctions JavaFunctions

echo "done with everything"
