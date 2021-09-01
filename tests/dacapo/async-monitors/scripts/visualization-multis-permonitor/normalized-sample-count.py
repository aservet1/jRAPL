#!/usr/bin/env python3

import os
# import json
# import numpy as np
# import matplotlib as mpl
import matplotlib.pyplot as plt
from sys import argv

from myutil import parse_cmdline_args, plt_set_axis_limits, put_bar_on_an_axis

result_dir, data_files = parse_cmdline_args(argv)
systemA_file, systemB_file = data_files

fig, (ax1, ax2) = plt.subplots (
  nrows=1,
  ncols=2,
  sharex=True,
  figsize=(5,3),
  sharey=True,
  constrained_layout=True
)
put_bar_on_an_axis( systemA_file, ax1, 'System A', color='purple', edgecolor='black', alpha=1 )
put_bar_on_an_axis( systemB_file, ax2, 'System B', color='purple', edgecolor='black', alpha=1 )

xrange = (None, None)
yrange = (.7, 1.2)
xaxis_precision, yaxis_precision = (0, 2)
plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

# fig.supxlabel('Monitor Type')
# fig.supylabel('Normalized Sample Count')

# plt.show()
fig.savefig(os.path.join(result_dir,'normalized-sample-count'))
print(" <.> done making the overall average graph")

