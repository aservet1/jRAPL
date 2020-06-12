#!/bin/bash

sudo modprobe msr

data=output

sudo java -cp /home/alejandro/jRAPL/src jrapl.GeneralTestDriver \
	| sed 's/[a-zA-DF-Z:]//g ; s/.// ; /^$/d' > $data

python3 plotting.py $data

rm $data
