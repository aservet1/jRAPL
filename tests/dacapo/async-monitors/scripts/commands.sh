#!/bin/bash

function echo_eval() {
	printf "\033[0;32m" "$@" "\033[0m \n" && eval "$@"
}

set -e

echo_eval ./scripts/analysis/memory-footprint.py jolteon-results-30/ results/memory-footprint/
echo_eval ./scripts/visualization/memory-footprint.py results/memory-footprint/memory-footprint.json results/memory-footprint/

echo_eval ./scripts/analysis/energy-per-sample_timenormalized.py jolteon-results-30/ results/energy-per-sample_timenormalized/
echo_eval ./scripts/visualization/energy-per-sample.py results/energy-per-sample_timenormalized/energy-per-sample.json results/energy-per-sample_timenormalized/

echo_eval ./scripts/analysis/energy-per-sample.py jolteon-results-30/ results/energy-per-sample/
echo_eval ./scripts/visualization/energy-per-sample.py results/energy-per-sample/energy-per-sample.json results/energy-per-sample/

echo_eval ./scripts/analysis/time-per-sample.py jolteon-results-30/ results/time-per-sample/
echo_eval ./scripts/visualization/time-per-sample.py results/time-per-sample/time-per-sample.json results/time-per-sample/

echo_eval ./scripts/analysis/sampling-efficiency.py jolteon-results-30/ results/sampling-efficiency/
echo_eval ./scripts/visualization/sampling-efficiency.py results/sampling-efficiency/sampling-efficiency.json results/sampling-efficiency/
