#!/bin/bash
cat /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor \
	| sort \
	| uniq
