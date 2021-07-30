#!/usr/bin/env python3

from sys import argv
import os
import json
import matplotlib.pyplot as plt
import matplotlib as mpl
import numpy as np

from myutil import parse_cmdline_args

def do_perbench(data, result_dir):    

    benchmarks = sorted(data['perbench'].keys())

    labels   = [ b for b in benchmarks ]
    
    java_avg = [  data['perbench'][b]['java']             ['avg']    for b in benchmarks  ]
    c_ll_avg = [  data['perbench'][b]['c-linklist']       ['avg']    for b in benchmarks  ]
    c_da_avg = [  data['perbench'][b]['c-dynamicarray']   ['avg']    for b in benchmarks  ]

    java_std = [  data['perbench'][b]['java']            ['stdev']   for b in benchmarks  ]
    c_ll_std = [  data['perbench'][b]['c-linklist']      ['stdev']   for b in benchmarks  ]
    c_da_std = [  data['perbench'][b]['c-dynamicarray']  ['stdev']   for b in benchmarks  ]

    ## Make the all-benchmarks graph ##
    bar_width = 0.25
    mpl.rcParams['figure.dpi'] = 600
    r1 = np.arange(len(c_ll_avg))
    r2 = [x + bar_width for x in r1]
    r3 = [x + bar_width for x in r2]

    plt.clf ()
    plt.barh(r1, c_da_avg, bar_width, xerr=c_da_std, color='#003f5c', edgecolor="white", label='C Dynamic Array')
    plt.barh(r2, c_ll_avg, bar_width, xerr=c_ll_std, color='#bc5090', edgecolor="white", label='C Linked List')
    plt.barh(r3, java_avg, bar_width, xerr=java_std, color='#ffa600', edgecolor="white", label='Java')

    plotinfo = data['plotinfo']['perbench']

    plt.ylabel('Benchmark', fontweight='bold')
    plt.xlabel(plotinfo['xlabel'], fontweight='bold')
    plt.yticks([r + bar_width for r in range(len(c_ll_avg))], labels)
    plt.legend()
    fig = plt.gcf()
    fig.set_size_inches(15,25)

    plt.savefig(os.path.join(result_dir, plotinfo['filename']))
    print(" <.> done making the perbench graph")

def do_overall(data, result_dir):
    
    plotinfo = data['plotinfo']['overall']

    overall_java_avg = data['overall']['java']            ['avg']
    overall_c_ll_avg = data['overall']['c-linklist']      ['avg']
    overall_c_da_avg = data['overall']['c-dynamicarray']  ['avg']

    overall_java_std = data['overall']['java']           ['stdev']
    overall_c_ll_std = data['overall']['c-linklist']     ['stdev']
    overall_c_da_std = data['overall']['c-dynamicarray'] ['stdev']

    plt.clf()
    plt.bar (                                                                     \
        x           =  [0,1,2],                                                   \
        height      =  [overall_java_avg, overall_c_ll_avg, overall_c_da_avg],    \
        yerr        =  [overall_java_std, overall_c_ll_std, overall_c_da_std],    \
        tick_label  =  ['java'          , 'c-linklist'    , 'c-dynamicarray'],    \
    )
        #capsize     =  .5                                                         \
    #)

    plt.ylabel(plotinfo['ylabel'])

    plt.savefig(os.path.join(result_dir, plotinfo['filename']))
    print(" <.> done making the overall average graph")

'''--------------------------------------------------------------------------------------'''

data_file, result_dir = parse_cmdline_args(argv)

with open(data_file) as fd:
	data = json.load(fd)

do_overall (data, result_dir)
do_perbench(data, result_dir)
