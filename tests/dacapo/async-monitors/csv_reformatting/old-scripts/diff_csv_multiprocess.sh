#!/bin/bash

dirs=$@
for dir in $dirs; do
    newdir=TRANSFORM_$dir
    mkdir -p $newdir
    for csvfile in $(ls $dir | grep '\.csv$'); do
        ./diff-csv.py $dir/$csvfile $newdir/$csvfile &
    done
done ; wait

