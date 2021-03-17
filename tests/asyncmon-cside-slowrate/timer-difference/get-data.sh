#!/bin/bash

if [ $# != 3 ]; then
	echo "usage: $0 <sleeprate> <iterations> <json outfile name>"
	exit 1
fi

s=$1
n=$2
outfile=$3

echo "{\"java\":$(java Jtimer $s $n) , \"c\":$(./ctimer $s $n) }" > $outfile

# echo C / J
# 
# for n in 1 2 4 8 16 32 64
# do
# 	for x in {1..100}
# 	do
# 		C=$(./ctimer $n)
# 		J=$(java Jtimer $n)
# 		echo $C / $J
# 	done
# 	echo $n---
# done
