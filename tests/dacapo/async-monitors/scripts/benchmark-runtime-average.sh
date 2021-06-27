#!/bin/bash

function msg_abort() {
	echo $1
	exit 2
}


[ -z $1 ] && msg_abort "usage: $0 <dacapo log file>"
logfile=$(pwd)/$1

dir=$(echo $0 | sed 's|/[^/]*$||'); cd $dir

grep '=' $logfile \
	| grep 'msec' \
	| sed 's/completed warmup [0-9][0-9]*//' \
	| sed 's/PASSED//' \
	| awk '{print $4 ":" $6}' \
	| python3 benchmark-runtime-average.py
