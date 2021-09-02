#!/usr/bin/env python3

import os
from sys import argv
import matplotlib.pyplot as plt

from myutil import plt_set_axis_limits, megaplot, validate_output_dir, get_data_files, output_dir

keypath = ['overall', 'normalized']

fig = megaplot(get_data_files('memory-footprint'), keypath=keypath, color='limegreen', edgecolor='black', alpha=1)

xrange = (None, None)
yrange = (.9, 1.1)
xaxis_precision, yaxis_precision = (0, 2)
plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

# fig.supxlabel('Monitor Type')
# fig.supylabel('Normalized Memory Footprint')

plt.show()
validate_output_dir(output_dir)
fig.savefig(os.path.join(output_dir,'memory-footprint'))
