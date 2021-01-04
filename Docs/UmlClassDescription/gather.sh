#!/bin/bash

for file in $(ls ../src/*.java)
do
	filename=$(echo $file | sed 's/^.*\///')
	python3 ClassDataCleanup.py $file > gathered/$filename
done
