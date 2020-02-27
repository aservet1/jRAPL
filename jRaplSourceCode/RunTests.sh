#!/bin/bash

sudo modprobe msr #make a way to test if this is necessary before repeatedly doing it (or maybe it doesn't matter)

case $1 in

	"alej")
		sudo java jrapl.DriverAlejandro
		;;
	"energycheck")
		sudo java jrapl.EnergyCheckUtils | grep 'dram'
		;;
	*)
		echo "current valid entries are 'alej' and 'energycheck'"
esac
