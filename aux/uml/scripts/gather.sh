#!/bin/bash

function remove_path_from_filename() {
	echo $1 | sed 's#^.*/##'
}

set -e

if [[ -z $1 ]]; then
	echo "usage: $0 <directory with all .java files>"
fi

outputdir=gathered
mkdir -p $outputdir
for file in $(ls $1/*.java)
do
	filename=$(remove_path_from_filename $file)
	python3 scripts/ClassDataCleanup.py $file > gathered/$filename
done
