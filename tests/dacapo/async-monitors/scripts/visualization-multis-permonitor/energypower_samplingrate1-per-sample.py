#!/usr/bin/env python3

import os
import matplotlib.pyplot as plt

from common import (
    plt_set_axis_limits,
    put_bar_on_an_axis,
    validate_output_dir,
    get_data_files,
    output_dir
)

def maketheplot(name,ytitle):
    keypath = ['overall', 'combined-socket']

    # fig = megaplot(
    #     get_data_files(name),
    #     keypath=keypath,
    #     color=('limegreen','gold'),
    #     edgecolor='black',
    #     alpha=1
    # )

    color = ('limegreen','gold')
    edgecolor='black'
    alpha=1
    data_files = get_data_files(name.replace('_samplingrate1',''))

    fig, axs = plt.subplots (
        nrows=1,
        ncols=2,
        sharex=True,
        sharey=True,
        figsize=(5,3),
        constrained_layout=True
    )
    put_bar_on_an_axis( data_files[0][0], axs[0], keypath=keypath, ylabel='Sampling Rate 1', title='System A', color=color, edgecolor=edgecolor, alpha=alpha, legend_loc='center right' )
    put_bar_on_an_axis( data_files[0][1], axs[1], keypath=keypath, ylabel=      None,        title='System B', color=color, edgecolor=edgecolor, alpha=alpha )

    xrange = (None, None)
    yrange = (0, .1)
    xaxis_precision, yaxis_precision = (0, 2)
    # plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

    fig.supxlabel('Monitor Type')
    fig.supylabel(ytitle)

    #plt.show()
    validate_output_dir(output_dir)
    fig.savefig(os.path.join(output_dir,name))


maketheplot('power-per-sample_samplingrate1','Power Per Sample (Watts)')
maketheplot('energy-per-sample_samplingrate1','Energy Per Sample (Joules)')