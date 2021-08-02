#!/bin/bash

if [ -z $1 ]; then
	echo "pass in log file as argument"
	exit 1
fi

log=$1

cat $log | grep 'in [0-9]* msec' | sed 's/^.*in //' | awk '{print $1}' | awk '{s+=$1} END {print s}'
