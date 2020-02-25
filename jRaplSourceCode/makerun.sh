#!/bin/bash

make javafiles

if [ $? == 0 ]
then
	sudo java jrapl.DriverAlejandro
fi
