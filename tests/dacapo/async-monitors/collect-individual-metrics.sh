#!/bin/bash

function echo_eval() {
    echo ")|) $@" && eval "$@"
}

set -e

metrics='sample-count sample-interval memory-footprint energy-per-sample power-per-sample'
samplingrates='1 2 4 8'
systems='SystemA SystemB'

for sys in $systems
do
    for m in $metrics
    do
        for r in $samplingrates
        do
            datadir=results/$sys/samplingrate_$r/intermediate-results/
            resultdir=results/$sys/samplingrate_$r/
            mkdir -p $resultdir
            echo_eval ./scripts/analysis/$m.py $datadir $resultdir
        done
    done
done

