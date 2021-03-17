#!/bin/bash

if [ $# != 3 ]; then
	echo "usage: $0 <sampling rate> <how many> <json outfile name>"
	exit 1
fi

s=$1; n=$2; outfile=$3

echo "{\"java\":$(java Jtimer $s $n),\"c\":$(./ctimer $s $n)}" > $outfile
