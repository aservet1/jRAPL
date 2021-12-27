#!/bin/bash
domain_namefiles=$(find /sys/class/powercap/intel-rapl/ | grep '\/name$')
for namefile in $domain_namefiles
do
	printf "%s\n\t%s\n\n" \
		$namefile \
		$(cat $namefile)
done
