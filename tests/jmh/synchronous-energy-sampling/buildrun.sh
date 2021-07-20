#!/bin/bash

function usage() {
	echo "usage: $0 data_dir"
	exit 2
}

set -e
sudo -v

[ -z $1 ] && usage $0

datadir=$1

if [ -d $datadir ]; then
	echo "data dir $datadir already exists. delete it or pick another name"
	exit 1
fi

mkdir $datadir

mvn clean install

./scripts/transferjars.sh \
	jRAPL-1.0.jar \
	target/benchmarks.jar


sudo java \
	-jar target/benchmarks.jar \
	-rf json \
	| tee $datadir/output.log

mv jmh-result.json $datadir

