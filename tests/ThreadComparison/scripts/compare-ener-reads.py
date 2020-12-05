from sys import argv
import statistics
import matplotlib.pyplot as plt
'''-----------------------------------------------'''
def diffs(data): # diffs[i] = data[i] - data[i-1]
	diff = list()
	for i in range(1,len(data)):
		diff.append(data[i]-data[i-1])
	return diff


def read_file_to_string(filename):
    fh = open(filename)
    data = fh.read()
    fh.close()
    return data

def make_numeric_array(data):
    data = data.split("\n")
    data = data[2:len(data)]
    data = list(filter(lambda x : x != '', data))
    data = [line.split(",") for line in data]
    data = [[float(item) for item in line] for line in data]
    data = [ line[1:-1] for line in data ] #dont need socket and timestamp
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
	data = [ diffs(d) for d in data ]
	return list(filter(lambda x: x != 0,data))
'''-----------------------------------------------'''
def bar_graph(cdata,jdata,title):
	langs = ["C","Java"]
	x_pos = [x for x in range(len(langs))]
	CTEs = [statistics.mean(cdata),statistics.mean(jdata)]
	error = [statistics.stdev(cdata),statistics.stdev(jdata)]

	# Build the plot
	fig, ax = plt.subplots()
	ax.bar(x_pos, CTEs, yerr=error, align='center', alpha=0.5, ecolor='black', capsize=10)
	ax.set_ylabel('average energy per sample (joules)')
	ax.set_xticks(x_pos)
	ax.set_xticklabels(langs)
	ax.set_title(title)
	ax.yaxis.grid(True)

	# Save the figure and show
	plt.tight_layout()
	plt.savefig("avg-sample-comparison_"+title.split()[len(title.split())-1])

'''-----------------------------------------------'''
if len(argv) != 3:
    print(f"usage: python3 {argv[0]} c_data_file java_data_file")
    exit(1)

cdata = read_file_to_string(argv[1])
jdata = read_file_to_string(argv[2])

#cdata or jdata, it doesn't matter. same header.
#currently getting rid of [1:-1] because socket and timestamp arent currently relevant
header = cdata.split('\n')[1].upper().split(',')[1:-1] 

cdata = remove_zeroes(group_by_column(make_numeric_array(cdata)))
jdata = remove_zeroes(group_by_column(make_numeric_array(jdata)))

#print(cdata)
#print(len(cdata),len(jdata))

for i in range(len(header)):

	bar_graph(cdata[i],jdata[i],"C vs Java Average Energy Per Nonzero Sample "+header[i])
	cmean = statistics.mean(cdata[i]);
	jmean = statistics.mean(jdata[i])
	cstdev = statistics.stdev(cdata[i]); jstdev = statistics.stdev(jdata[i])
	print("Nonzero energy sample picked up by thread (joules): C // Java")
	print(f"  mean: \t{cmean} // {jmean}")
	print(f"  stdev:\t{cstdev} // {jstdev}")
	print(f"cdata[{header[i]}] sample size: {len(cdata[i])}")
	print(f"jdata[{header[i]}] sample size: {len(jdata[i])}")
	print("-------------------------------")

