#!/bin/bash

function log() {
	echo " ..)-> $1"
}

log ""
for script in memory-footprint \
              sample-count     \
              sample-interval  \
              energypower-per-sample
do
	log "starting on $script"
	./scripts/visualization-multis-permonitor/$script.py
	log "done with   $script"
	log ""
done

