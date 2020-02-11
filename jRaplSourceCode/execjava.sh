#!/bin/bash

## compile then run if no compliation error #

source=$1
run=${source//.java/}

javac *.java

if [ $? == 0 ]
then
	java $run
fi
