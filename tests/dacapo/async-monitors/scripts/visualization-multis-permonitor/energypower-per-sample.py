#!/usr/bin/env python3

import os

from common import (
    plt_set_axis_limits,
    megaplot,
    validate_output_dir,
    get_data_files,
    output_dir
)

def maketheplot(name,ytitle):
    keypath = ['overall', 'combined-socket']

    fig = megaplot(
        get_data_files(name),
        keypath=keypath,
        color=('limegreen','gold'),
        edgecolor='black',
        alpha=1
    )

    xrange = (None, None)
    yrange = (0, .1)
    xaxis_precision, yaxis_precision = (0, 2)
    # plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

    fig.supxlabel('Monitor Type')
    fig.supylabel(ytitle)

    #plt.show()
    validate_output_dir(output_dir)
    fig.savefig(os.path.join(output_dir,name))


maketheplot('power-per-sample','Power Per Sample (Watts)')
maketheplot('energy-per-sample','Energy Per Sample (Joules)')