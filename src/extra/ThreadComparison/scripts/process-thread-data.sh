#!/bin/bash

[ -z $1 ] && exit 1

datafile=$1
filename=${datafile/.tmp/}

cat $datafile | sed '/^delay.*$/d' | awk 'BEGIN {FS="\t"}; {print $1}' | sed 's/[a-z: ]//g' | python zero-reading-interval.py > stats/nonzero-data-dram_$filename.stats
cat $datafile | sed '/^delay.*$/d' | awk 'BEGIN {FS="\t"}; {print $2}' | sed 's/[a-z: ]//g' | python zero-reading-interval.py > stats/nonzero-data-core_$filename.stats
cat $datafile | sed '/^delay.*$/d' | awk 'BEGIN {FS="\t"}; {print $3}' | sed 's/[a-z: ]//g' | python zero-reading-interval.py > stats/nonzero-data-pkg_$filename.stats

for f in $(ls stats/*.stats)
 do
	echo "<<<===============((($f | $filename)))===============>>>"
	cat $f
 done
