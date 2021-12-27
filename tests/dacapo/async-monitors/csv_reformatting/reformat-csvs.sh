#!/bin/bash

base="/home/alejalej/storage/async-monitor-experiments"
dirs="SystemB/samplingrate_1/"

# SystemB/samplingrate_2/
# SystemB/samplingrate_4/
# SystemB/samplingrate_8/"

for d in $dirs; do
	mkdir -p $d
	./diff-csv.py $base/$d $d
done
