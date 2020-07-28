#!/bin/bash

#processit= $( sed '/^delay.*$/d' | awk 'BEGIN {FS="\t"}; {print $1}' | sed 's/[a-z: ]*//g' )

echo $(	cat dump-c.tmp | sed '/^delay.*$/d' | awk 'BEGIN {FS="\t"}; {print $1}' | sed 's/[a-z: ]*//g'; 	\
	echo '@@@@@'; 											\
	cat dump-j.tmp | sed '/^delay.*$/d' | awk 'BEGIN {FS="\t"}; {print $1}' | sed 's/[a-z: ]*//g' ) \
	| python3 compare-ener-reads.py									\
	> $1
