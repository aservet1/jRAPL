#!/bin/bash

make javafiles

if [ $? == 0 ]
then
	java jrapl.DriverAlejandro
fi
