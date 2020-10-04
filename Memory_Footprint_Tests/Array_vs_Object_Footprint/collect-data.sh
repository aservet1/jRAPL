#!/bin/bash


representations="Array Object"
dataFolder=~/jRAPL/Memory_Footprint_Tests/Array_vs_Object_Footprint/data

cd ~/jRAPL/src

for n in {1..10}
do
	for representation in $representations
	do
		sudo java jrapltesting.MemoryTestUtils $representation 5000 2> /dev/null > "$dataFolder/$representation$n.dump"
		echo -n "did $representation$n  "
	done
	echo ''
done


cd $dataFolder

#python3 plot_memory_trend.py $(ls *.dump)
