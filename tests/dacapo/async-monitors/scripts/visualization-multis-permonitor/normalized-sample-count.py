#!/usr/bin/env python3

import os
# import json
# import numpy as np
# import matplotlib as mpl
import matplotlib.pyplot as plt
from sys import argv

from myutil import parse_cmdline_args, plt_set_axis_limits, megaplot

result_dir, data_files, keypath = parse_cmdline_args(argv)

fig = megaplot(data_files, keypath=keypath, color='purple', edgecolor='black', alpha=1)

xrange = (None, None)
yrange = (.7, 1)
xaxis_precision, yaxis_precision = (0, 2)
plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

# fig.supxlabel('Monitor Type')
# fig.supylabel('Normalized Sample Count')

plt.show()
fig.savefig(os.path.join(result_dir,'normalized-sample-count'))

