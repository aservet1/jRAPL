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

def extract_energy_stats(data):
	data = data.split("\n")
	data = data[2:] #remove two header items
	data = list(filter(lambda x : x != '', data))
	data = [line.split(",") for line in data]
	data = [[float(item) for item in line] for line in data]
	data = [ line[1:-1] for line in data ] #dont need socket FOR NOW since this entire script is only assuming one socket (must fix if this is being done on multi socket machines)
	return data

def extract_timestamps(data):
	data = data.split("\n")
	data = data[2:] #remove two header items
	data = [ x for x in data if x != '' ]
	data = [int(line.split(",")[-1]) for line in data]
	return data

def time_btwn_nonzero_energy(energy, timestamps):
	nonz_indices = list()
	energy = diffs(energy)
	for i in range(len(energy)):
		if energy[i] == 0:
			nonz_indices.append(i)
	timestamps = [timestamps[i] for i in nonz_indices]
	return diffs(timestamps)

def group_by_column(data):
	columns = list()
	for i in range(len(data[0])):
		columns.append([line[i] for line in data])
	return columns

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
	ax.set_ylabel('avg time between nonzero (J)')
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

#cdata or jdata, it doesn't matter. same header. for all of them
#currently getting rid of [1:-1] because socket and timestamp arent currently relevant
header = jdata.split('\n')[1].upper().split(',')[1:-1] 

c_dynarr_energy = group_by_column( extract_energy_stats(c_dynarr_data))
c_dynarr_timestamps = extract_timestamps(c_dynarr_data)
c_dynarr_timebtwn = [ time_btwn_nonzero_energy( energy, c_dynarr_timestamps ) for energy in c_dynarr_energy ]

c_linklist_energy = group_by_column( extract_energy_stats(c_linklist_data))
c_linklist_timestamps = extract_timestamps(c_linklist_data)
c_linklist_timebtwn = [ time_btwn_nonzero_energy( energy, c_linklist_timestamps ) for energy in c_linklist_energy ]

j_energy = group_by_column( extract_energy_stats(jdata))
j_timestamps = extract_timestamps(jdata)
j_timebtwn = [ time_btwn_nonzero_energy( energy, j_timestamps ) for energy in j_energy ]

fig, (axs) = plt.subplots(2,2)
fig.suptitle("laskdjf;lsakjf "+','.join(header))

i = 0
for ax in axs.flat:

	powerDomain = header[i]

	bar_graph(ax,c_dynarr_energy[i],c_linklist_energy[i],j_energy[i],header[i])

	c_dynarr_mean = statistics.mean(c_dynarr_timebtwn[i])
	c_dynarr_stdev = statistics.stdev(c_dynarr_timebtwn[i])

	c_linklist_mean =  statistics.mean(c_linklist_timebtwn[i])
	c_linklist_stdev = statistics.mean(c_linklist_timebtwn[i])

	jmean  = statistics.mean(j_timebtwn[i])
	jstdev = statistics.stdev(j_timebtwn[i])

	print("Average time between nonzero energy readings: C Linked List // C Dynamic Array // Java")
	print(f"  mean: \t{c_linklist_mean} // {c_dynarr_mean} // {jmean}")
	print(f"  stdev:\t{c_linklist_stdev} // {c_dynarr_stdev} // {jstdev}")
	print(f"c_dynarr_energy[{header[i]}] sample size: {len(c_dynarr_energy[i])}")
	print(f"c_linklist_energy[{header[i]}] sample size: {len(c_linklist_energy[i])}")
	print(f"j_energy[{header[i]}] sample size: {len(j_energy[i])}")
	print("-------------------------------")

	i += 1

plt.savefig('avg-time-between-nonzero-reading')
