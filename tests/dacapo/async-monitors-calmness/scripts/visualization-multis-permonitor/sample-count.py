#!/usr/bin/env python3

import os
from sys import argv
import matplotlib.pyplot as plt

from common import plt_set_axis_limits, megaplot, get_data_files, output_dir, validate_output_dir

def normalized():
    keypath = ['overall', 'normalized']

    fig = megaplot(get_data_files('sample-count'), keypath=keypath, color='purple', edgecolor='black', alpha=1)

    xrange = (None, None)
    yrange = (.7, 1.1)
    xaxis_precision, yaxis_precision = (0, 2)
    plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

    fig.supxlabel('Monitor Type')
    fig.supylabel('Normalized Sample Count')

    #plt.show()
    validate_output_dir(output_dir)
    fig.savefig(os.path.join(output_dir,'normalized-sample-count'))

def observed():
    keypath = ['overall', 'observed']

    fig = megaplot(get_data_files('sample-count'), keypath=keypath, color='purple', edgecolor='black', alpha=1)

    xrange = (None, None)
    yrange = (.7, 1.1)
    xaxis_precision, yaxis_precision = (0, 2)
    # plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

    fig.supxlabel('Monitor Type')
    fig.supylabel('Observed Sample Count')

    #plt.show()
    validate_output_dir(output_dir)
    fig.savefig(os.path.join(output_dir,'observed-sample-count'))

normalized()
observed()
