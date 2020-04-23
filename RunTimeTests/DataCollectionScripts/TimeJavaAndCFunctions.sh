#!/bin/bash

if [ "$#" != 2 ]
then
	echo "Usage $0 [number of trials] [name to tag onto results folder]"
	exit 1
fi

trials=4000
name=$2

./DataCollectionScripts/TimeJavaOrCFunctions.sh c $trials $name

./DataCollectionScripts/TimeJavaOrCFunctions.sh java $trials $name

cd RuntimeResults

pwd

python3 ../DataCollectionScripts/bar_graphs.py CFunctions JavaFunctions
