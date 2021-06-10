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
# level reported assumed already grouped by column.
#
# zero read could also be because no energy change, but thats very unlikely
# there's usually at least a little energy increase
def remove_zeroes(data):
	data = [ diffs(d) for d in data ]
	return list(filter(lambda x: x != 0,data))
'''-----------------------------------------------'''
def bar_graph(ax,c_dynarr_data,c_linklist_data,jdata,title):
	langs = ["C-LL","C-DA","Java"]
	x_pos = [x for x in range(len(langs))]
	CTEs = [statistics.mean(c_dynarr_data),statistics.mean(c_linklist_data),statistics.mean(jdata)]
	error = [statistics.stdev(c_dynarr_data),statistics.stdev(c_linklist_data),statistics.stdev(jdata)]

	# Build the plot
	ax.bar(x_pos, CTEs, yerr=error, align='center', alpha=0.5, ecolor='black', capsize=10)
	ax.set_ylabel('avg energy (J)')
	ax.set_xticks(x_pos)
	ax.set_xticklabels(langs)
	ax.set_title(title)
	ax.yaxis.grid(True)

	plt.tight_layout()

'''-----------------------------------------------'''
if len(argv) != 4:
    print(f"usage: python3 {argv[0]} c-dynamicarray_data_file c-linklist_data_file java_data_file")
    exit(1)

c_dynarr_data = read_file_to_string(argv[1])
c_linklist_data = read_file_to_string(argv[2])
jdata = read_file_to_string(argv[3])

#cdata or jdata, it doesn't matter. same header.
#currently getting rid of [1:-1] because socket and timestamp arent currently relevant
header = c_dynarr_data.split('\n')[1].upper().split(',')[1:-1] 

c_dynarr_data = remove_zeroes(group_by_column(make_numeric_array(c_dynarr_data)))
c_linklist_data = remove_zeroes(group_by_column(make_numeric_array(c_linklist_data)))
jdata = remove_zeroes(group_by_column(make_numeric_array(jdata)))

fig, (axs) = plt.subplots(2,2)
fig.suptitle("C vs Java Average Energy Per Nonzero Sample "+','.join(header))

i = 0
for ax in axs.flat:
	powerDomain = header[i]

	bar_graph(ax,c_dynarr_data[i],c_linklist_data[i],jdata[i],header[i])

	c_dynarr_mean = statistics.mean(c_dynarr_data[i])
	c_dynarr_stdev = statistics.stdev(c_dynarr_data[i])

	c_linklist_mean =  statistics.mean(c_linklist_data[i])
	c_linklist_stdev = statistics.mean(c_linklist_data[i])

	jmean  = statistics.mean(jdata[i])
	jstdev = statistics.stdev(jdata[i])

	cstdev = statistics.stdev(c_dynarr_data[i])
	print("Nonzero energy sample picked up by thread (joules): C Linked List // C Dynamic Array // Java")
	print(f"  mean: \t{c_linklist_mean} // {c_dynarr_mean} // {jmean}")
	print(f"  stdev:\t{c_linklist_stdev} // {c_dynarr_stdev} // {jstdev}")
	print(f"c_dynarr_data[{header[i]}] sample size: {len(c_dynarr_data[i])}")
	print(f"c_linklist_data[{header[i]}] sample size: {len(c_linklist_data[i])}")
	print(f"jdata[{header[i]}] sample size: {len(jdata[i])}")
	print("-------------------------------")

	i += 1

plt.savefig('avg-nonzero-energy-read')
