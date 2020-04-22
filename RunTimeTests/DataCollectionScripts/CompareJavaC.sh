#!/bin/bash

trials=4000

sudo modprobe msr
cd ../../jRaplSourceCode
# assumes the java and c files are properly set up for Java side timing
sudo java jrapl.RuntimeTestUtils --time-java-calls $trials |	\
	grep -E '():' |			\
	sed 's/.*---/------------------------------/'		\
	> ../RunTimeTests/java-time-output.data
