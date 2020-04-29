#!/bin/bash

DataCollectionScripts/TimeJavaAndCFunctions.sh 4000 QuinnyBoi
DataCollectionScripts/TimeMSRReadings.sh 4000 QuinnyBoi

cd ../src

make

sudo java jrapl.EnergyCheckUtils
