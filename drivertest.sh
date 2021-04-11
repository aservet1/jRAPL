#!/bin/bash

sudo -v

# runs through my sample main() drivers to make sure they're all in order

printf "\nhello w0rld\n\n";

runDriver="sudo java -cp java/target/jRAPL-1.0.jar "

for driver in 'ArchSpec' 'SyncEnergyMonitor' 'AsyncEnergyMonitor C' 'AsyncEnergyMonitor Java'
do
	echo ~~~$driver~~~
	$runDriver jRAPL.$driver
	echo =============================================
done
