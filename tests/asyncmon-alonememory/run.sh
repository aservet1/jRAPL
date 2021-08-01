#!/bin/bash

function echo_eval() {
	cmd="$@"
	echo $cmd
	eval $cmd
}

args=$@
echo_eval sudo java -cp "jRAPL-1.0.jar:." Driver $args
