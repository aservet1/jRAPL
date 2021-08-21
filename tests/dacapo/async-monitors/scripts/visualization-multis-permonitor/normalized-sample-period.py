#!/usr/bin/env python3

from sys import argv
import os
import json
import matplotlib.pyplot as plt
import matplotlib as mpl
import numpy as np

from myutil import parse_cmdline_args, plt_set_axis_limits, put_bar_on_an_axis

result_dir, data_files = parse_cmdline_args(argv)

result_dir, data_files = parse_cmdline_args(argv)
systemA_file, systemB_file = data_files
assert len(data_files) == 2
systemA_file, systemB_file = data_files

fig, (ax1, ax2) = plt.subplots (
  nrows=1,
  ncols=2,
  sharex=True,
  sharey=True,
  figsize=(5,3),
  constrained_layout=True
)

xrange = (None, None)
yrange = (900, 1350)
xaxis_precision, yaxis_precision = (0, 0)
plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

put_bar_on_an_axis(systemA_file, ax1, 'System A', color='purple', edgecolor='black', alpha=1 )
put_bar_on_an_axis(systemB_file, ax2, 'System B', color='purple', edgecolor='black', alpha=1 )

fig.supxlabel('Monitor Type')
fig.supylabel('Observed Sampling Interval (μs)')

plt.savefig(os.path.join(result_dir, 'time-per-sample'))
print(" <.> done making the overall average graph")
