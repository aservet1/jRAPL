#!/bin/bash

function echo_eval() {
    echo ")|) $@" && eval "$@"
}

set -e

metrics='energy-per-sample power-per-sample sample-count sample-interval memory-footprint'
samplingrates='1 2 4'
systems='SystemA SystemB'

for sys in $systems
do
    for m in $metrics
    do
        for r in $samplingrates
        do
            datadir=results/$sys/samplingrate_$r/intermediate-results/
            resultdir=results/$sys/samplingrate_$r/$m/
            mkdir -p $resultdir
            echo_eval ./scripts/analysis/$m.py $datadir $resultdir
        done
    done
done

