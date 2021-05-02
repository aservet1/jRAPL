#!/bin/bash

cmd='sudo java
	-Djdk.attach.allowAttachSelf
	-cp jol-cli-0.9-full.jar:jRAPL-1.0.jar:.
	ObjectMemory'
#echo $cmd; echo;
eval $cmd
