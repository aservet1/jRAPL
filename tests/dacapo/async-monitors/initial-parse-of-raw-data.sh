#!/bin/bash

function echo_eval() {
    echo "))) $@" && eval "$@"
}

set -e

samplingrates='8'
systems='SystemA SystemB'

for system in $systems; do
    for r in $samplingrates; do
		#echo_eval \
		#	ln -s \
		#	~/storage/async-monitor-experiments/$system/samplingrate_$r \
		#	./results/$system/samplingrate_$r/raw
        echo_eval \
			./scripts/analysis/analysis-periteration.py \
			./results/$system/samplingrate_$r/raw \
			./results/$system/samplingrate_$r/intermediate-results
    done
done
