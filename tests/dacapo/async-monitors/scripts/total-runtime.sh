#!/bin/bash

if [ -z $1 ]; then
	echo "pass in log file as argument"
	exit 1
fi

log=$1

msec=$(cat $log | grep 'in [0-9]* msec' | sed 's/^.*in //' | awk '{print $1}' | awk '{s+=$1} END {print s}')

hours=$(( $msec / 1000 / 60 / 60 ))
minutes=$(( $msec / 1000 / 60 % 60 ))

echo "$hours hours and $minutes minutes"
echo "  (not including non-dacapo overhead, like writing data to files after iterations)"
