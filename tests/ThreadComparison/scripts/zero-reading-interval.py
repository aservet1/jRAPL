from sys import argv
from sys import stdin
from statistics import mean, stdev, mode, StatisticsError
import matplotlib.pyplot as plt
'''---------------------------------------------------------------'''
''' Terminology I made up: A "zero interval" is the number of	 '''
''' zero-joule energy readings between any two nonzero energy	 '''
''' readings. In the context of this experiment, it indicates	 '''
''' that the MSR has not been updated, as the before/after energy '''
''' stamps were the same exact value.							 '''
'''You should definitely timestamp your readings to see why there could possibly be zero intervals of value 0, since thats way too quick
of an update, since it takes much less than 1 ms for two consecutive readings. Could be that there was a system interrupt between
those two readings, making a delay anomany between them actually be longer than 1 ms'''
'''---------------------------------------------------------------'''
def get_zero_intervals(data):
	zero_intervals = list()
	zero_count = 0
	for i in range(1,len(data)):
		if data[i]-data[i-1] == 0: zero_count += 1
		else:
			zero_intervals.append(zero_count)
			zero_count = 0
	if zero_count != 0: zero_intervals.append(zero_count)
	return zero_intervals

'''---------------------------------------------------------------'''
def make_histogram(data,title): # data[0]: cdata // data[1] jdata 

	plt.xlabel("zero intervals")
	plt.ylabel("frequency")
	plt.title(title)

	plt.hist(data,20,histtype='bar',label=('C','Java'))
	plt.legend(loc="upper right")

	parts = title.split(); category = parts[-1]
	plt.savefig("zero-intervals_"+category)
	plt.clf()
'''---------------------------------------------------------------'''
def print_stats(data,title):
	print(f"{title}:")
	print("  mean: "+str(mean(data)))
	try:
		print("  stdev: "+str(stdev(data)))
	except StatisticsError:
		print("  stdev: N/A (variance requires at least two data points)") 
	print("  mode: "+str(mode(data)))
	print("  min: "+str(min(data)))
	print("  max: "+str(max(data)))
	print("  num zero intervals: "+str(len(data)))
'''---------------------------------------------------------------'''
def read_file_to_string(filename):
	fh = open(filename)
	data = fh.read()
	fh.close()
	return data

def make_numeric_array(data):
	data = data.split("\n")
	data = data[2:len(data)] #remove headers
	data = list(filter(lambda x : x != '', data))
	data = [line.split(",") for line in data]
	data = [[float(item) for item in line] for line in data]
	data = [row[1:-1] for row in data] #remove the first and last. socket and timestamp arent relevant (for now)
	return data

def group_by_column(data):
	columns = list()
	for i in range(len(data[0])):
		columns.append([line[i] for line in data])
	return columns

# zero readings just mean the next reading hasn't been updated in
# the register so they are irrelevant to comparing the actual energy
# level reported assumed already grouped by column
def remove_zeroes(data):
	return list(filter(lambda x: x != 0,data))
'''-----------------------------------------------'''

"""right now we're just assuming it's one socket"""

if len(argv) != 3:
	print(f"usage: python3 {argv[0]} cdata javadata")
	exit(1)

cdata = read_file_to_string(argv[1])
jdata = read_file_to_string(argv[2])

#cdata or jdata, it doesn't matter. same header.
#currently getting rid of [1:-1] because socket and timestamp arent currently relevant
header = cdata.split('\n')[1].upper().split(',')[1:-1] 

cdata = group_by_column(make_numeric_array(cdata))
jdata = group_by_column(make_numeric_array(jdata))

for i in range(len(header)):
	c_zero_intervals = get_zero_intervals(cdata[i])
	j_zero_intervals = get_zero_intervals(jdata[i])

	make_histogram([c_zero_intervals,j_zero_intervals],"C vs Java Zero Intervals "+header[i])

	print_stats(c_zero_intervals,title=f"C Zero Interval Stats {header[i]}")
	print("  -------------------------------------  ")
	print_stats(j_zero_intervals,title=f"Java Zero Interval Stats {header[i]}")
	print("==========================================")
print(f"Total C readings:\t{len(cdata[0])}")
print(f"Total Java readings:\t{len(jdata[0])}")



