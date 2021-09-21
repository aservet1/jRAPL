#!/bin/bash

function echo_eval() {
	echo "))) $@" && eval "$@"
}

set -e

samplingrates='1 2 4 8'
systems='SystemA SystemB'
aggs='perbench permonitor'

for sys in $systems; do
	for r in $samplingrates; do
		for ag in $aggs; do
			echo_eval ./scripts/analysis/aggregate-$ag.py results/$sys/samplingrate_$r/intermediate-results
		done
	done
done
