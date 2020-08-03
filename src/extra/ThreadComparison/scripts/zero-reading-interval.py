from sys import argv
from sys import stdin
from statistics import mean, stdev, mode
'''------------------------------------'''

stdin_data = stdin.readlines()

data = [float(d.strip()) for d in stdin_data]
sample_size = len(data)

print("success")
#print(data)

zero_intervals = list()
zero_count = 0
for d in data:
	if d == 0: zero_count += 1
	else:
		zero_intervals.append(zero_count)
		zero_count = 0

#print(sorted(zero_intervals))
#TODO -- make a histogram
print("Zero Intervals:")
print("  mean: "+str(mean(zero_intervals)))
print("  stdev: "+str(stdev(zero_intervals)))
print("  mode: "+str(mode(zero_intervals)))
print("  min: "+str(min(zero_intervals)))
print("  max: "+str(max(zero_intervals)))
print("  num zero readings: "+str(len(zero_intervals)))


nonzero_data = filter(lambda d: d != 0,data)
#print(nonzero_data)

print("Nonzero Readings (joules):")
print("  mean: "+str(mean(nonzero_data)))
print("  stdev: "+str(stdev(nonzero_data)))
print("  mode: "+str(mode(nonzero_data)))
print("  min: "+str(min(nonzero_data)))
print("  max: "+str(max(nonzero_data)))
print("  num nonzero readings: "+str(len(nonzero_data)))

print("Sample Size: "+str(sample_size))











