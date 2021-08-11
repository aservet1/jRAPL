#!/usr/bin/env python3

import os
import json
import numpy as np
import matplotlib as mpl
import matplotlib.pyplot as plt
from sys import argv

from myutil import parse_cmdline_args

'''----------------------------------------------------------------'''

def do_row(axs, data, power_domains, title):

    for ax, powd in zip(axs.flat, power_domains):

        java_avg = data[powd]['java'          ] ['avg']
        c_ll_avg = data[powd]['c-linklist'    ] ['avg']
        c_da_avg = data[powd]['c-dynamicarray'] ['avg']
        java_std = data[powd]['java'          ]['stdev']
        c_ll_std = data[powd]['c-linklist'    ]['stdev']
        c_da_std = data[powd]['c-dynamicarray']['stdev']

        labels = ['java', 'c-linklist', 'c-dynamicarray']

        ax.bar (
            x           =  [0,1,2],
            height      =  [java_avg, c_ll_avg, c_da_avg],
            yerr        =  [java_std, c_ll_std, c_da_std],
            tick_label  =  labels,
            capsize     =  1,
            color = 'gold',
            edgecolor = 'black',
            alpha = 1
        )

        ax.set_title(powd.replace('_', ' ').title())

'''----------------------------------------------------------------'''

result_dir, data_files = parse_cmdline_args(argv)

with open(data_files[0]) as fd:
	data1 = json.load(fd)['overall']
with open(data_files[1]) as fd:
	data2 = json.load(fd)['overall']

power_domains = sorted(data1.keys())

fig = plt.figure(constrained_layout=True)
i = 0

row_names = ['System A', 'System B']
subfigs = fig.subfigures (
    nrows=len(row_names),
    ncols=1
)
for row, subfig in enumerate(subfigs):
    subfig.suptitle(row_names[row])

    # create 1x4 subplots per subfig
    axs = subfig.subplots(nrows=1, ncols=4, sharex=True, sharey=True)
    for powd, ax in zip(power_domains,axs):
        java_avg = data[powd]['java'          ] ['avg']
        c_ll_avg = data[powd]['c-linklist'    ] ['avg']
        c_da_avg = data[powd]['c-dynamicarray'] ['avg']
        java_std = data[powd]['java'          ]['stdev']
        c_ll_std = data[powd]['c-linklist'    ]['stdev']
        c_da_std = data[powd]['c-dynamicarray']['stdev']

        labels = ['java', 'c-linklist', 'c-dynamicarray']

        ax.bar (
            x           =  [0,1,2],
            height      =  [java_avg, c_ll_avg, c_da_avg],
            yerr        =  [java_std, c_ll_std, c_da_std],
            tick_label  =  labels,
            capsize     =  1,
            color = 'gold',
            edgecolor = 'black',
            alpha = 1
        )

        ax.set_title(powd.replace('_', ' ').title())

plt.show()
fig.savefig(os.path.join(result_dir, 'energy-per-sample'))
print(" <.> done making the overall average graph")

