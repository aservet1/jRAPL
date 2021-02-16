#!/bin/bash

sudo modprobe msr

if [ "$#" -lt 1 ] || [ "$#" -gt 4 ]; then
	echo "Usage: $0 name number_of_iterations [mem | cpu | both | dacapo [dacapo_benchmark_name]]" >&2
	exit 1
fi

outputdir=""

#if [ "$3" = "mem" ]; then
#	outputdir="stressed_"$3
#	stress-ng --vm 2 --vm-bytes 50% --all 0 --timeout 60s >/dev/null 1>&2 &
#fi
#
#if [ "$3" = "cpu" ]; then
#	outputdir="stressed_"$3
#	stress-ng --cpu 2 --all	0 --timeout 60s >/dev/null 1>&2 &
#fi
#
#if [ "$3" = "both" ]; then
#	outputdir="stressed_"$3
#	stress-ng --vm 2 --cpu 2 --vm-bytes 50% --all 0 --timeout 180s >/dev/null 1>&2 &
#fi

if [ "$3" = "dacapo" ]; then
	outputdir="DaCapo_"$4
	java -jar ./DataCollectionPrograms/dacapo-9.12-MR1-bach.jar $4 -n 100 >/dev/null 1>&2 &
	sleep 15
fi

if [ "$outputdir" = "" ]; then
	outputdir="idleCPU"
fi

outputdir="EnergyData_$1/"$outputdir
sudo rm -rf $outputdir
mkdir -p $outputdir

sudo java -cp ~/jRAPL/java/target/jRAPL-1.0.jar jRAPLTesting.BenchmarkingEnergy --read-energy-values $2 > $outputdir/Output

cd $outputdir

powerDomains="DRAM GPU CORE PKG"

for f in $powerDomains
do
	file=$f.data
	grep $f Output > $f.data
	[ -s $f.data ] || rm $f.data # remove data file if it's empty. probably empty because power domain not supported
done

python3 ../../DataCollectionPrograms/CalcEnergyStats.py

#rm -f ../CORE.data ../PACKAGE.data ../DRAM.data
