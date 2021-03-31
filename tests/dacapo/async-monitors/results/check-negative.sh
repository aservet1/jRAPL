#!/bin/bash

for filename in $(ls *.memory.json)
do
	v=$(cat $filename | json_pp | grep '-')
	echo "$filename: $v"
done
