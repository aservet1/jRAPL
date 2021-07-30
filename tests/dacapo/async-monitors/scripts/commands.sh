#!/bin/bash

set -e

./scripts/analysis/memory-footprint.py jolteon-results-30/ results/memory-footprint/
./scripts/visualization/memory-footprint.py results/memory-footprint/memory-footprint.json results/memory-footprint/

./scripts/analysis/energy-per-sample_timenormalized.py jolteon-results-30/ results/energy-per-sample_timenormalized/
./scripts/visualization/energy-per-sample.py results/energy-per-sample_timenormalized/energy-per-sample.json results/energy-per-sample_timenormalized/

./scripts/analysis/energy-per-sample.py jolteon-results-30/ results/energy-per-sample/
./scripts/visualization/energy-per-sample.py results/energy-per-sample/energy-per-sample.json results/energy-per-sample/

./scripts/analysis/time-per-sample.py jolteon-results-30/ results/time-per-sample/
./scripts/visualization/time-per-sample.py results/time-per-sample/time-per-sample.json results/time-per-sample/

./scripts/analysis/sampling-efficiency.py jolteon-results-30/ results/sampling-efficiency/
./scripts/visualization/sampling-efficiency.py results/sampling-efficiency/sampling-efficiency.json results/sampling-efficiency/
