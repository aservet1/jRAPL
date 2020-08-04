#!/bin/bash

#################################################################
 # remove non energy reading data / text from dump file and echo 
 # column1
 #  ##### 
 # column2
 #  #####
 # column3
function parseDumpFile()
{
    file=$1
    for i in {1..3}
     do
        cat $file |\
            sed '/^delay.*$/d' |\
            awk -v i="$i" 'BEGIN {FS="\t"}; {print $i};' |\
            sed 's/[a-z: ]*//g';
            [ $i == 3 ] || echo "#####"
     done
}
#################################################################

if [ -z $1 ] || [ -z $2 ]
then
    echo "usage: $0 c-data-file java-data-file"
    exit
fi

cd ~/jRAPL/src/extra/ThreadComparison

cdump=$1
jdump=$2

parseDumpFile $cdump
echo '@@@@@'
parseDumpFile $jdump





