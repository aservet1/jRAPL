from sys import stdin
from statistics import mean

d = {}

for line in stdin.readlines():
	k,v = line.split(":")
	v = int(v.strip())
	if k in d.keys():
		d[k].append(v)
	else: d[k] = [v]

for k in sorted(d.keys()):
	print(k,":",int(mean(d[k])),"msec")
