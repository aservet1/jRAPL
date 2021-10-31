#!/bin/bash
energy_stat_command="sudo cat /sys/class/powercap/intel-rapl:0/energy_uj"
a=$(eval $energy_stat_command)
sleep 1
b=$(eval $energy_stat_command)
c=$(echo "print(($b - $a)/1000000)" | python3)
echo $c
