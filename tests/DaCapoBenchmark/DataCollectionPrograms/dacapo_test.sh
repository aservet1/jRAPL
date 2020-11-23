cd ~/dacapo

for bench in $(java Harness -l)
do
    java Harness $bench -n 1 >/dev/null
done


