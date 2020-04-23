#!/bin/bash

all_graphs=$(find RuntimeResults_System{A,B} -name "*.png")

java=$(echo "$all_graphs" | grep /JavaFunctions/)
c=$(echo "$all_graphs" | grep /CFunctions/)
msr=$(echo "$all_graphs" | grep /PerSocketMSRReadings/)

echo "$java"
