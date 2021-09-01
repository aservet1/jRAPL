#!/bin/bash

function echo_eval() {
    echo "))) $@" && eval "$@"
}

set -e

samplingrates='1 2 4'
systems='SystemA SystemB'

for sys in $systems; do
    for r in $samplingrates; do
        echo_eval ./scripts/analysis/aggregate-perbench.py   results/$sys/samplingrate_$r/intermediate-results
        echo_eval ./scripts/analysis/aggregate-permonitor.py results/$sys/samplingrate_$r/intermediate-results
    done
done