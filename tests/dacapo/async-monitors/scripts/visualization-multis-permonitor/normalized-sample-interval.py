#!/usr/bin/env python3

from sys import argv
import os
import json
import matplotlib.pyplot as plt
import matplotlib as mpl
import numpy as np

from myutil import megaplot, parse_cmdline_args, plt_set_axis_limits

result_dir, data_files, keypath = parse_cmdline_args(argv)

fig = megaplot(data_files, keypath=keypath, color='blue', edgecolor='black', alpha=1)

xrange = (None, None)
yrange = (.9, 1.4)
xaxis_precision, yaxis_precision = (0, 0)
plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

# fig.supxlabel('Monitor Type')
# fig.supylabel('Normalized Sample Interval (μs)')

plt.show()
fig.savefig(os.path.join(result_dir, 'normalized-sample-interval'))
print(" <.> done making the overall average graph")
