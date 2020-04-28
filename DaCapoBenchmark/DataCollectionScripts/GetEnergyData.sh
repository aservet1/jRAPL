#!/bin/bash

sudo modprobe msr

if [ "$#" -lt 1 ] || [ "$#" -gt 4 ]; then
	echo "Usage: $0 name number_of_iterations [mem | cpu | both | dacapo [dacapo_benchmark_name]]" >&2
	exit 1
fi

outputdir=""

if [ "$3" = "mem" ]; then
	outputdir="stressed_"$3
	stress-ng --vm 2 --vm-bytes 50% --all 0 --timeout 60s >/dev/null 1>&2 &
fi

if [ "$3" = "cpu" ]; then
	outputdir="stressed_"$3
	stress-ng --cpu 2 --all	0 --timeout 60s >/dev/null 1>&2 &
fi

if [ "$3" = "both" ]; then
	outputdir="stressed_"$3
	stress-ng --vm 2 --cpu 2 --vm-bytes 50% --all 0 --timeout 180s >/dev/null 1>&2 &
fi

if [ "$3" = "dacapo" ]; then  #xalan tradesoap tradebeans avrova fop h2 jython luindex lusearch lusearch-fix pmd sunflow - working # eclipse batik tomcat - not working
	outputdir="DaCapo_"$4
	jrapl_dir=$PWD
#	if [ ! -d $HOME"/dacapo/" ]; then
#		echo "Directory ~/dacapo/ DOES NOT exist." && exit 1
#	fi
#	cd $HOME/dacapo/
#	java Harness $4 -n 100 >/dev/null 1>&2 &
	java -jar ~/dacapo/dacapo-9.12-MR1-bach.jar $4 -n 100 >/dev/null 1>&2 &
	sleep 15
	cd $jrapl_dir
fi

if [ "$outputdir" = "" ]; then
	outputdir="idleCPU"
fi

outputdir="EnergyData_$1/"$outputdir

mkdir -p $outputdir

cd ../src/

sudo java jrapl.RuntimeTestUtils --read-energy-values $2 >../DaCapoBenchmark/$outputdir/Output

cd ../DaCapoBenchmark/$outputdir/

powerDomains="DRAM CORE PACKAGE"

for f in $powerDomains
do
	file=$f.data
	grep $f Output > $f.data
done

python3 ../../DataCollectionScripts/CalcEnergyStats.py

#rm -f ../CORE.data ../PACKAGE.data ../DRAM.data
