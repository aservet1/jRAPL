#!/bin/bash

function usage() {
	echo "invalid option: $1"
	echo "must be JavaSide, CSide, or readMSR"
	exit 2
}

[ -z $1 ] && usage
[ $1 != 'JavaSide' ] && [ $1 != 'CSide' ] && [ $1 != 'readMSR' ] && usage

files=$1_*_histogram.data
python3 scripts/make_histogram.py $files
