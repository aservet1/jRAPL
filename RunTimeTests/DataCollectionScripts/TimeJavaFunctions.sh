#!/bin/bash

trials=4000

sudo modprobe msr
cd ../jRaplSourceCode
# assumes the java and c files are properly set up for Java side timing
sudo java jrapl.DriverAlejandro $trials |			\
	grep -E 'Results|Avg|StDev|---' |		\
	sed 's/.*---/------------------------------/'	\
	> ../TimingFunctionCalls/java-AVG_STDEV.data
