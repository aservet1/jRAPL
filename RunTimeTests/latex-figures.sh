#!/bin/bash

all_graphs=$(find RuntimeResults_System{A,B} -name "*.png")

echo "$all_graphs" > temp.txt
python3 latex-figures.py temp.txt
rm temp.txt

#java=$(echo "$all_graphs" | grep /JavaFunctions/)
#c=$(echo "$all_graphs" | grep /CFunctions/)
#msr=$(echo "$all_graphs" | grep /PerSocketMSRReadings/)
