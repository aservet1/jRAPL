#!/bin/bash

sudo -v

mycallback=BenchmarkTimer
dacapo_jar="dacapo-evaluation-git+309e1fa.jar"
classpath="$dacapo_jar:."

benchmarks=$(sed 's/#.*$//' benchmarks.txt) #whats-wrong.txt )

make clean all

source benchmark_size_assoc

for benchmark in $benchmarks
do
	size=${benchmark_size_assoc[$benchmark]}
	echo " .>> $(date) running $benchmark size $size ..."
	sudo java -cp $classpath Harness $benchmark -c $mycallback -n 1 -s $size
done
