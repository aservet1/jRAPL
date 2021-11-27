#!/bin/bash 
## Assumes the parameters I usually use, the only thing needed to specify is the output directory

function usage() {
	echo "usage: $1 <result directory> [optional log file]"
	exit 1
}

function run_dacapo() {
	mycallback=CalmnessCallback
	dacapo_jar="dacapo-evaluation-git+309e1fa.jar"
	jRAPL_jar="jRAPL-1.0.jar"
	classpath="$dacapo_jar:$jRAPL_jar:."

	benchmark=$1;	monitoringEnergy=$2;	
	iterations=$3;	warmups=$4;	
	monitorType=$5;	samplingRate=$6;	
	resultDir=$7;	

	mkdir -p $resultDir

	source scripts/benchmark_size_assoc
	size=${benchmark_size_assoc[$benchmark]}

	echo "@run_dac po@@ @ benchmark -> $benchmark @ @"
	echo " r n_daca   @@  monitoringEnergy -> $monitoringEnergy @.@"
	echo "@run_da apo@ @  iterations -> $iterations @ @"
	echo "@r n_daca o @@@ warmups -> $warmups @.@"
	echo " run dac po@  @ monitorType -> $monitorType @ @"
	echo "@r n_d capo@@ @ resultDir -> $resultDir @.@"
	echo "@run_dacapo@@@@ size -> $size @ @"

	sudo java \
		-DmonitoringEnergy=$monitoringEnergy \
		-Dwarmups=$warmups \
		-DmonitorType=$monitorType \
		-DresultDir=$resultDir \
		-DsamplingRate=$samplingRate \
		-cp $classpath Harness \
		  $benchmark \
		-c $mycallback \
		-n $iterations \
		-s $size
}

set -e

[ $# = 0 ] && usage $0
resultDir=$1

warmups=1 #5
iterations=3 #30

### Validating logfile and resultDir args
[ -z $2 ] && logfile=/dev/null || logfile=$2
if [ -d $resultDir ]; then
	echo " ERROR: directory '$resultDir' already exists."
	echo "    please pick another name, or manually delete it (and be sure that you want to delete it AND that it's the one you actually want to delete / not a typo)"
	exit 1
fi
if [ -f $logfile ] && [ $logfile != /dev/null ]; then
	echo "ERROR: logfile '$logfile' already exists!"
	exit 1
fi
###

touch $logfile

echo " (||(.. Started on $(date)" | tee -a $logfile

sudo -v

mkdir $resultDir

make clean all | tee -a $logfile

for samplingRate in 1 2 4 8
do
	for benchmark in $(sed 's/#.*$//g' benchmarks.txt)
	do
		monitoringEnergy=true
		for monitorType in c-dynamicarray c-linklist java
		do
			run_dacapo \
				$benchmark $monitoringEnergy $iterations \
				$warmups $monitorType $samplingRate $resultDir
		done

		monitoringEnergy=false
		run_dacapo \
			$benchmark $monitoringEnergy $iterations \
			$warmups no-monitor $samplingRate $resultDir
	done
	printf 'Subject: Your jRAPL calmness experiments for sampling rate %s are done.\n\nOn to the next one.' $samplingRate | sudo ssmtp a.l.servetto@gmail.com
done 2>&1 | tee -a $logfile

echo " (||(.. Completed on $(date)" | tee -a $logfile

printf 'Subject: Your jRAPL calmness experiments are done.\nYou can log on to %s to collect your data\n' $(hostname) | sudo ssmtp a.l.servetto@gmail.com

