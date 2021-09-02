#!/usr/bin/env python3.6

import os
from sys import argv
import matplotlib.pyplot as plt

from myutil import megaplot, plt_set_axis_limits, get_data_files, output_dir, validate_output_dir

keypath = ['overall', 'observed']

fig = megaplot(get_data_files('sample-interval'), keypath=keypath, color='purple', edgecolor='black', alpha=1)

# xrange = (None, None)
# yrange = (1000, 5000)
# xaxis_precision, yaxis_precision = (0, 0)
# plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

# fig.supxlabel('Monitor Type')
# fig.supylabel('Normalized Sample Interval (μs)')

plt.show()
validate_output_dir(output_dir)
fig.savefig(os.path.join(output_dir, 'observed-sample-interval'))
