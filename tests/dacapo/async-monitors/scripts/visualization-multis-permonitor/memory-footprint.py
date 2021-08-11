#!/usr/bin/env python3

import os
import json
import statistics
import numpy as np
import pandas as pd
import matplotlib as mpl
import matplotlib.pyplot as plt
from math import sqrt
from sys import argv

from myutil import parse_cmdline_args, put_bar_on_an_axis

result_dir, data_files = parse_cmdline_args(argv)
systemA_file, systemB_file = data_files
assert len(data_files) == 2
systemA_file, systemB_file = data_files

fig, (ax1, ax2) = plt.subplots (
  nrows=1,
  ncols=2,
  sharex=True,
  sharey=True,
  figsize=(5,3)
)
put_bar_on_an_axis(systemA_file, ax1, 'System A', color='limegreen', edgecolor='black', alpha=1 )
put_bar_on_an_axis(systemB_file, ax2, 'System B', color='limegreen', edgecolor='black', alpha=1 )

plt.savefig(os.path.join(result_dir,'memory-footprint'))
