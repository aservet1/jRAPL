#!/bin/bash

cd ..
# assumes the java and c files are properly set up for java side timing
sudo java jrapl.DriverAlejandro 1000 |			\
	grep -E 'Results|Avg|StDev|---' |		\
	sed 's/.*---/------------------------------/'	\
	> alejandro-test-place/java-AVG_STDEV.data
