cat 25iters-output.log | grep 'in [0-9]* msec' | sed 's/^.*in //' | awk '{print $1}' | awk '{s+=$1} END {print s}'
