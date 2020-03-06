#!/bin/bash

sudo modprobe msr #make a way to test if this is necessary before repeatedly doing it (or maybe it doesn't matter)

case $1 in

	"alej")
		sudo java jrapl.DriverAlejandro | grep -E 'Avg|StDev|Results'
		;;
	"echeck")
		echo 'this will do an infinite loop. press Ctrl+C to exit.'
		echo -n 'press enter to start: '
		read
		sudo java jrapl.EnergyCheckUtils | grep -E 'dram|cpu|package'
		;;
	*)
		echo "current valid entries are 'alej' and 'echeck'"
		exit 1
esac
