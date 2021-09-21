#!/usr/bin/env python3

import os
from sys import argv
import matplotlib.pyplot as plt

from common import plt_set_axis_limits, megaplot, validate_output_dir, get_data_files, output_dir

def normalized():
    keypath = ['overall', 'normalized']

    fig = megaplot(get_data_files('memory-footprint'), keypath=keypath, color='steelblue', edgecolor='black', alpha=1)

    xrange = (None, None)
    yrange = (.9, 1.1)
    xaxis_precision, yaxis_precision = (0, 2)
    plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

    fig.supxlabel('Monitor Type')
    fig.supylabel('Normalized Memory Footprint')

    #plt.show()
    validate_output_dir(output_dir)
    fig.savefig(os.path.join(output_dir,'normalized-memory-footprint'))

def percent_difference():
    keypath = ['overall', 'percentdiff']

    fig = megaplot(get_data_files('memory-footprint'), keypath=keypath, color='steelblue', edgecolor='black', alpha=1)

    xrange = (None, None)
    yrange = (.9, 1.1)
    xaxis_precision, yaxis_precision = (0, 2)
    # plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

    fig.supxlabel('Monitor Type')
    fig.supylabel('Memory Footprint (% difference)')

    #plt.show()
    validate_output_dir(output_dir)
    fig.savefig(os.path.join(output_dir,'percentdiff-memory-footprint'))

def raw_bars():
    keypath = ['overall', 'raws']

    fig = megaplot(get_data_files('memory-footprint'), keypath=keypath, color=('steelblue','firebrick'), edgecolor='black', alpha=1)

    xrange = (None, None)
    yrange = (.9, 1.1)
    xaxis_precision, yaxis_precision = (0, 2)
    # plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

    fig.supxlabel('Monitor Type')
    fig.supylabel('Memory Footprint (bytes)')

    #plt.show()
    validate_output_dir(output_dir)
    fig.savefig(os.path.join(output_dir,'rawbars-memory-footprint'))

raw_bars()
normalized()
percent_difference()
