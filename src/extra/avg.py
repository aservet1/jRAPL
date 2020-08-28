from statistics import mean, stdev
from sys import stdin

data = stdin.readlines()
data = [int(d) for d in data]
for d in data: print(d)
print("mean:\t",mean(data))
print("stdev:\t",stdev(data))
