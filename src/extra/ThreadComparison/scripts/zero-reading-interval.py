from sys import argv
from sys import stdin
from statistics import mean, stdev, mode
import matplotlib.pyplot as plt
'''---------------------------------------------------------------'''
''' Terminology I made up: A "zero interval" is the number of     '''
''' zero-joule energy readings between any two nonzero energy     '''
''' readings. In the context of this experiment, it indicates     '''
''' that the MSR has not been updated, as the before/after energy '''
''' stamps were the same exact value.                             '''
'''You should definitely timestamp your readings to see why there could possibly be zero intervals of value 0, since thats way too quick
of an update, since it takes much less than 1 ms for two consecutive readings. Could be that there was a system interrupt between
those two readings, making a delay anomany between them actually be longer than 1 ms'''
'''---------------------------------------------------------------'''
def get_zero_intervals(data):
	zero_intervals = list()
	zero_count = 0
	for d in data:
		if d == 0: zero_count += 1
		else:
			zero_intervals.append(zero_count)
			zero_count = 0
	return zero_intervals
	
'''---------------------------------------------------------------'''
def make_histogram_list(zero_intervals):
	histogram = [0]*(max(zero_intervals)+1)
	for n in zero_intervals:
		histogram[n] += 1
	return histogram
'''---------------------------------------------------------------'''
def make_histogram_graph(histogram_list): # this is a super janky function my dude
	intervals = list(range(len(histogram_list)))
	
	plt.xlabel("zero intervals")
	plt.ylabel("frequency")
	plt.title("Freqency of consecutive zero readings")

	plt.bar(intervals,histogram_list)
	plt.savefig("temp")
'''---------------------------------------------------------------'''
def print_stats(data,title):
    print(f"{title}:")
    print("  mean: "+str(mean(zero_intervals)))
    print("  stdev: "+str(stdev(zero_intervals)))
    print("  mode: "+str(mode(zero_intervals)))
    print("  min: "+str(min(zero_intervals)))
    print("  max: "+str(max(zero_intervals)))
    print("  num zero readings: "+str(len(zero_intervals)))
'''---------------------------------------------------------------'''
stdin_data = stdin.readlines()

data = [float(d.strip()) for d in stdin_data]
sample_size = len(data)

zero_intervals = get_zero_intervals(data)

histogram_list = make_histogram_list(zero_intervals)

make_histogram_graph(histogram_list)

print_stats(zero_intervals,title="Zero Interval Stats")

'''
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
'''
