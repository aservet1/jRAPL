#!/bin/bash

all_graphs=$(find RuntimeResults_x -name "*.png")

echo "$all_graphs" > ___temp.txt
python3 latex-figures.py ___temp.txt
rm ___temp.txt

#java=$(echo "$all_graphs" | grep /JavaFunctions/)
#c=$(echo "$all_graphs" | grep /CFunctions/)
#msr=$(echo "$all_graphs" | grep /PerSocketMSRReadings/)
