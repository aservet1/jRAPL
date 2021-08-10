#!/usr/bin/env python3
import os
from sys import argv
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle

def trunc_str(n): # decimal tostring, truncated
    return "%.4f" % n

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
    exit(1)
subplot_rows = len(data_files) // 3
subplot_cols = 3

clr = 'mediumslateblue'
edgeclr = 'black'
a = 1

fig, axs = plt.subplots( subplot_rows, subplot_cols, figsize=(3,2) )
for i, fname in enumerate( data_files ):
    ax = axs.flat[i]
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

    # print(fname)
    # print(filtered.keys(), filtered.values())
    # print(outliers.keys(), outliers.values())

    ax.bar(list(filtered.keys()), filtered.values(), color = clr, edgecolor = edgeclr, alpha = a)
    extra1 = Rectangle((0, 0), 1, 1, fc="w", fill=False, edgecolor='none', linewidth=0)
    extra2 = Rectangle((0, 0), 1, 1, fc="w", fill=False, edgecolor='none', linewidth=0)
    ax.legend()
    title = fname.split('.')[0].split('/')[-1].split('_')[1]
    ax.set_title(title)
    ax.legend([extra1, extra2], ( "x̄: "+trunc_str(mean)+"µs", "σ: "+trunc_str(sd)+"µ" ))

# fig.supxlabel("microseconds".title())
# fig.supylabel("frequency".title())
plt.show()

fig.savefig (
	os.path.join (
		output_dir,
		"all-dem-msr-runtimes"
	)
)
