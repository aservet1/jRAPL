#!/usr/bin/env python3

import os
from sys import argv
import matplotlib.pyplot as plt

from common import megaplot, plt_set_axis_limits, get_data_files, output_dir, validate_output_dir

def observed():
    keypath = ['overall', 'observed']

    fig = megaplot(get_data_files('sample-interval'), keypath=keypath, color='blue', edgecolor='black', alpha=1)

    # xrange = (None, None)
    # yrange = (1000, 5000)
    # xaxis_precision, yaxis_precision = (0, 0)
    # plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

    fig.supxlabel('Monitor Type')
    fig.supylabel('Observed Sample Interval (μs)')

    #plt.show()
    validate_output_dir(output_dir)
    fig.savefig(os.path.join(output_dir, 'observed-sample-interval'))

def normalized():
    keypath = ['overall', 'normalized']

    fig = megaplot(get_data_files('sample-interval'), keypath=keypath, color='blue', edgecolor='black', alpha=1)

    xrange = (None, None)
    yrange = (.9, 1.4)
    xaxis_precision, yaxis_precision = (0, 0)
    plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

    fig.supxlabel('Monitor Type')
    fig.supylabel('Normalized Sample Interval')

    #plt.show()
    validate_output_dir(output_dir)
    fig.savefig(os.path.join(output_dir, 'normalized-sample-interval'))

normalized()
observed()
