#!/usr/bin/env python3

import os
import matplotlib.pyplot as plt
from sys import argv

from myutil import parse_cmdline_args, plt_set_axis_limits, megaplot

result_dir, data_files, keypath = parse_cmdline_args(argv)

fig = megaplot(data_files, keypath=keypath, color='limegreen', edgecolor='black', alpha=1)

xrange = (None, None)
yrange = (.9, 1.1)
xaxis_precision, yaxis_precision = (0, 2)
plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

# fig.supxlabel('Monitor Type')
# fig.supylabel('Normalized Memory Footprint')

plt.show()
fig.savefig(os.path.join(result_dir,'memory-footprint'))
