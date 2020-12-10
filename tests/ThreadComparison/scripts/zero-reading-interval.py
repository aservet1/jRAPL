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
#def make_histogram(ax,data,title): # data[0]: cdata // data[1] jdata 

	#plt.xlabel("zero intervals")
	#plt.ylabel("frequency")
	#plt.title(title)

	#ax.hist(data,20,histtype='bar',label=('C','Java'))
	#ax.legend(loc="upper right")

	##parts = title.split(); category = parts[-1]
	##plt.savefig("zero-intervals_"+category)
	##plt.clf()
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

fig, (axs) = plt.subplots(2,2, gridspec_kw={'hspace':0.5,'wspace':0.5})
fig.suptitle('Zero interval histograms for ' + ','.join(header))

numbins = 10

i = 0
for ax in axs.flat:
	powerDomain = header[i]

	c_zerointervals = get_zero_intervals(cdata[i])
	j_zerointervals = get_zero_intervals(jdata[i])
	ax.hist([c_zerointervals,j_zerointervals], bins=numbins, alpha=0.5, label=['C','Java'] if i == 0 else [])
	ax.set_title(powerDomain)

	print_stats(c_zerointervals,title=f"C Zero Interval Stats {powerDomain}")
	print("  -------------------------------------  ")
	print_stats(j_zerointervals,title=f"Java Zero Interval Stats {powerDomain}")
	print("==========================================")

	ax.set(xlabel='size of zero interval',ylabel='frequency')
	i += 1

fig.legend()
plt.savefig('zero-intervals')

print(f"Total C readings:\t{len(cdata[0])}")
print(f"Total Java readings:\t{len(jdata[0])}")

