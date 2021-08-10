#!/bin/bash

function echo_eval() {
	# printf "\033[0;32m" "$@" "\033[0m \n" && eval "$@"
	echo  "$@" && eval "$@"
}

set -e

if [[ -z $1 ]]; then
	echo "usage: $0 alejtpad-e15|jolteon"
fi

# todo: consider making this also do the intermediate aggr-per* data instead of just the end metrics
# todo: make this a loop, not a buncha different calls to it

who=$1

echo_eval \
	./scripts/analysis/memory-footprint.py \
		results/$who/intermediate-results \
		results/$who/memory-footprint/
echo_eval \
	./scripts/visualization/memory-footprint.py \
	results/$who/memory-footprint/memory-footprint.json \
	results/$who/memory-footprint/

echo_eval \
	./scripts/analysis/energy-per-sample_timenormalized.py \
	results/$who/intermediate-results \
	results/$who/energy-per-sample_timenormalized/
echo_eval \
	./scripts/visualization/energy-per-sample.py \
	results/$who/energy-per-sample_timenormalized/energy-per-sample.json \
	results/$who/energy-per-sample_timenormalized/

echo_eval \
	./scripts/analysis/energy-per-sample.py \
	results/$who/intermediate-results \
	results/$who/energy-per-sample/
echo_eval \
	./scripts/visualization/energy-per-sample.py \
	results/$who/energy-per-sample/energy-per-sample.json \
	results/$who/energy-per-sample/

echo_eval \
	./scripts/analysis/time-per-sample.py \
	results/$who/intermediate-results \
	results/$who/time-per-sample/
echo_eval \
	./scripts/visualization/time-per-sample.py \
	results/$who/time-per-sample/time-per-sample.json \
	results/$who/time-per-sample/

echo_eval \
	./scripts/analysis/sampling-efficiency.py \
	results/$who/intermediate-results \
	results/$who/sampling-efficiency/
echo_eval \
	./scripts/visualization/sampling-efficiency.py \
	results/$who/sampling-efficiency/sampling-efficiency.json \
	results/$who/sampling-efficiency/
