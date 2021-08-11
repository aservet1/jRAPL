#!/usr/bin/env python3

import os
import json
import numpy as np
import matplotlib as mpl
import matplotlib.pyplot as plt
from sys import argv

from myutil import parse_cmdline_args

result_dir, data_files = parse_cmdline_args(argv)

fds  = [ open(df) for df in data_files ]
data = [ json.load(fd)['overall'] for fd in fds ]
fds  = [ fd.close() for fd in fds ]

power_domains = sorted(data[0].keys())

fig = plt.figure(constrained_layout=True, figsize=(8,7))

row_names = [
    'System A',
    'System B'
]
subfigs = fig.subfigures (
    nrows=len(row_names),
    ncols=1
)
for row, subfig in enumerate(subfigs):
    subfig.suptitle(row_names[row])

    # create 1x4 subplots per subfig
    axs = subfig.subplots (
        nrows=1, ncols=4,
        sharex=True, sharey=True
    )
    for powd, ax in zip(power_domains,axs):
        java_avg = data[row][powd][     'java'     ][ 'avg' ]
        c_ll_avg = data[row][powd][  'c-linklist'  ][ 'avg' ]
        c_da_avg = data[row][powd]['c-dynamicarray'][ 'avg' ]
        java_std = data[row][powd][     'java'     ]['stdev']
        c_ll_std = data[row][powd][  'c-linklist'  ]['stdev']
        c_da_std = data[row][powd]['c-dynamicarray']['stdev']
        labels   = ['J', 'C-L', 'C-D']
        ax.bar (
            x           =  [0,1,2],
            height      =  [java_avg, c_ll_avg, c_da_avg],
            yerr        =  [java_std, c_ll_std, c_da_std],
            tick_label  =  labels,
            #capsize     =  1,
            color       = 'gold',
            edgecolor   = 'black',
            alpha       =  1
        )
        ax.set_title(powd.replace('_', ' ').title())

fig.supxlabel('Monitor Type')
fig.supylabel('Average Energy Per Sample (joules)')

# plt.show()
fig.savefig(os.path.join(result_dir, 'energy-per-sample'))
print(" <.> done making the overall average graph")
