#!/bin/bash

function helloworld() {
	for (( i = 1; i <= $1; i++ ))
	do
		echo $i hello w0rld
	done
}

max=5

iterations=$1
cycles=0
leftover=$iterations

if [ $iterations -gt $max ]; then
	cycles=$(( $iterations / $max ))
	leftover=$(( $iterations % $max))
fi

for (( i = 1; i <= $cycles; i++ ))
do
	helloworld $max
	echo '<break>'
done

helloworld $leftover
