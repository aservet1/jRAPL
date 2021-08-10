#!/usr/bin/env python3
import os
from sys import argv
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle

'''------------------------------------------------------------------------------'''

# def trunc_str(n): # decimal tostring, truncated
#     return "%.4f" % n

def gather_histogram_info(fname):
    data = {}
    with open(fname) as fh:
        n = 0
        sum_ = 0
        for line in fh:
            k, v = [int(s.strip()) for s in line.split()]
            data[k] = v
            n += v
            sum_ += k*v
        mean = sum_/n
        sq_sum = 0
        for time in data:
            sq_sum += ((time - mean)**2)*data[time]
        sd = (sq_sum / n)**0.5
        filtered = {}
        outliers = {}
        for time in data:
            if time <= mean + 3*sd or time <= mean - 3*sd:
                filtered[time] = data[time]
            else:
                outliers[time] = data[time]

    return filtered, outliers, mean, sd

'''------------------------------------------------------------------------------'''

try:
    data_files = argv[1:-1]
    output_dir = argv[-1]
except IndexError:
    print("usage:",argv[0],"<list of data files> output_dir")
    exit(2)
if not len(data_files):
    print("usage:",argv[0],"<list of data files> output_dir")
    exit(2)

if len(data_files) % 3 != 0:
    print(data_files,len(data_files))
    print("error: this is going to be an _ x 3 subplot figure, so each group of 3 data files must correspond to a row of the subplots")
    print('  (you still might get index out of bounds errors if you pass in the wrong number of things, this is a personal script not a fooolproof consumer grade software piece. think about why that happened if you get the index error)')
    exit(1)


fig = plt.figure(constrained_layout=True)
i = 0
# create 3x1 subfigs (credit to: https://stackoverflow.com/questions/27426668/row-titles-for-matplotlib-subplot)
row_names = ['System A', 'System B']
subfigs = fig.subfigures (
    nrows=len(row_names),
    ncols=1
)
for row, subfig in enumerate(subfigs):
    subfig.suptitle(row_names[row])

    # create 1x3 subplots per subfig
    axs = subfig.subplots(nrows=1, ncols=3, sharex=True, sharey=True)
    for col, ax in enumerate(axs):
        fname = data_files[i]
        filtered, outliers, mean, sd = gather_histogram_info(fname); i+=1
        ax.bar (
            list(filtered.keys()),
            filtered.values(),
            color = 'mediumslateblue',
            edgecolor = 'black',
            alpha = 1
        )
        title = fname.split('.')[0].split('/')[-1].split('_')[1]
        ax.set_title(title)#f'Plot title {col}')

fig.supylabel('Frequency')
fig.supxlabel('Run Time (usec)')

plt.show()

fig.savefig (
	os.path.join (
		output_dir,
		"all-msr-runtimes"
	)
) ## todo: make sure the picture actually fits on a page, width-wise