#!/bin/bash

base="/home/alejalej/storage/async-monitor-experiments"
dirs="
SystemA/samplingrate_1/
SystemA/samplingrate_2/
SystemA/samplingrate_4/
SystemA/samplingrate_8/
SystemB/samplingrate_1/
SystemB/samplingrate_2/
SystemB/samplingrate_4/
SystemB/samplingrate_8/
"

for d in $dirs; do
	mkdir -p $d
	sudo cp -v $base/$d/*.json $d &
done

wait
