#!/bin/bash

set -e

./scripts/visualization-multis-permonitor/normalized-sample-count.py \
    results/overall-plots \
    results/System{A,B}/samplingrate_1/sample-count.json \
    results/System{A,B}/samplingrate_2/sample-count.json \
    results/System{A,B}/samplingrate_4/sample-count.json \
    overall normalized

./scripts/visualization-multis-permonitor/normalized-sample-interval.py \
    results/overall-plots \
    results/System{A,B}/samplingrate_1/sample-interval.json \
    results/System{A,B}/samplingrate_2/sample-interval.json \
    results/System{A,B}/samplingrate_4/sample-interval.json \
    overall normalized

./scripts/visualization-multis-permonitor/memory-footprint.py \
    results/overall-plots \
    results/System{A,B}/samplingrate_1/memory-footprint.json \
    results/System{A,B}/samplingrate_2/memory-footprint.json \
    results/System{A,B}/samplingrate_4/memory-footprint.json \
    overall normalized

