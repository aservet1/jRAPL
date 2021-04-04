#!/bin/bash

for i in 0 1 2 4 8 16 32 64
do
	./get-data.sh $i 5000 results/result-$i.json
	echo "$(date) | done with 5000 samples for sleep rate $i"
done

#echo "{\"java\":$(java Jtimer $s $n),\"c\":$(./ctimer $s $n)}" > $outfile
