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

from aggr_utils import aggr_mean, aggr_stdev

def percent_diff(a,b):
    return (a-b)/((a+b)/2)

def percent_diff_stdev(sa,sb,a,b): # uses propagation of error through each step of a percent difference
    subtract = a-b
    subtract_sd = sqrt((sa**2)+(sb**2))
    average = (a+b)/2
    average_sd = sqrt(.5**2 * sa**2 + .5**2 * sb**2)
    return sqrt( ((subtract/average)**2) * ( (subtract_sd/subtract)**2 + (average_sd/average)**2 ))

'''---------------------------------------------------------------------------------------------------'''

outputfile = 'memory-percent-difference.json'
data_dir = argv[1]
os.chdir(data_dir)
files = sorted([ f for f in os.listdir() if f.endswith('.aggregate-perbench.json') ])

data = []
for fname in files:
    with open(fname) as f:
        d = json.loads(f.read())
        data.append(d)
tmp = {}
for d in data:
    bench = d['metadata']['benchmark']
    if not bench in tmp.keys(): tmp[bench] = [d]
    else: tmp[bench].append(d)
data = tmp

results = {}
results['perbench'] = {}
results['overall'] = {}

for benchmark in sorted(data.keys()):

    def get_by_monitor_type(data, monitor_type):
        return [ d for d in data[benchmark] if d['metadata']['monitor_type'] == monitor_type ][0]

    javg = get_by_monitor_type(data, 'java')['memory']['jraplon']['avg']
    jstd = get_by_monitor_type(data, 'java')['memory']['jraplon']['stdev']

    clavg = get_by_monitor_type(data, 'c-linklist')['memory']['jraplon']['avg'] 
    clstd = get_by_monitor_type(data, 'c-linklist')['memory']['jraplon']['stdev']

    cdavg = get_by_monitor_type(data, 'c-dynamicarray')['memory']['jraplon']['avg']  
    cdstd = get_by_monitor_type(data, 'c-dynamicarray')['memory']['jraplon']['stdev']

    nojavg = get_by_monitor_type(data, 'c-dynamicarray')['memory']['jraploff']['avg']
    nojstd = get_by_monitor_type(data, 'c-dynamicarray')['memory']['jraploff']['stdev']

    results['perbench'][benchmark] = {}
    results['perbench'][benchmark]['java'] = {}
    results['perbench'][benchmark]['java']['avg'] = percent_diff(javg, nojavg)
    results['perbench'][benchmark]['java']['stdev'] = percent_diff_stdev(jstd, nojstd, javg, nojavg)
    results['perbench'][benchmark]['java']['numSamples'] = get_by_monitor_type(data, 'java')['metadata']['numSamples']['avg']



    results['perbench'][benchmark]['c-linklist'] = {}
    results['perbench'][benchmark]['c-linklist']['avg'] = percent_diff(clavg, nojavg)
    results['perbench'][benchmark]['c-linklist']['stdev'] = percent_diff_stdev(clstd, nojstd, clavg, nojavg)
    results['perbench'][benchmark]['c-linklist']['numSamples'] = get_by_monitor_type(data, 'c-linklist')['metadata']['numSamples']['avg']


    results['perbench'][benchmark]['c-dynamicarray'] = {}
    results['perbench'][benchmark]['c-dynamicarray']['avg'] = percent_diff(cdavg, nojavg)
    results['perbench'][benchmark]['c-dynamicarray']['stdev'] = percent_diff_stdev(cdstd, nojstd, cdavg, nojavg)
    results['perbench'][benchmark]['c-dynamicarray']['numSamples'] = get_by_monitor_type(data, 'c-dynamicarray')['metadata']['numSamples']['avg']

## ---------- Now to average across all benchmarks  ------------ ##

java_avg = [ results['perbench'][benchmark]['java']['avg'] for benchmark in results['perbench'].keys() ]
java_std = [ results['perbench'][benchmark]['java']['stdev'] for benchmark in results['perbench'].keys() ]
c_ll_avg = [ results['perbench'][benchmark]['c-linklist']['avg'] for benchmark in results['perbench'].keys() ]
c_ll_std = [ results['perbench'][benchmark]['c-linklist']['stdev'] for benchmark in results['perbench'].keys() ]
c_da_avg = [ results['perbench'][benchmark]['c-dynamicarray']['avg'] for benchmark in results['perbench'].keys() ]
c_da_std = [ results['perbench'][benchmark]['c-dynamicarray']['stdev'] for benchmark in results['perbench'].keys() ]

java_numsamples = [ results['perbench'][benchmark]['java']['numSamples'] for benchmark in results['perbench'].keys() ]
c_ll_numsamples = [ results['perbench'][benchmark]['c-linklist']['numSamples'] for benchmark in results['perbench'].keys() ]
c_da_numsamples = [ results['perbench'][benchmark]['c-dynamicarray']['numSamples'] for benchmark in results['perbench'].keys() ]

results['overall'] = {}
results['overall']['java'] = {}
results['overall']['c-linklist'] = {}
results['overall']['c-dynamicarray'] = {}

results['overall']['java']['avg'] = aggr_mean (java_numsamples, java_avg)
results['overall']['java']['stdev'] = aggr_stdev(java_numsamples, java_std)
results['overall']['c-linklist']['avg'] = aggr_mean (c_ll_numsamples, c_ll_avg)
results['overall']['c-linklist']['stdev'] = aggr_stdev(c_ll_numsamples, c_ll_std)
results['overall']['c-dynamicarray']['avg'] = aggr_mean (c_da_numsamples, c_da_avg)
results['overall']['c-dynamicarray']['stdev'] = aggr_stdev(c_da_numsamples, c_da_std)

with open(outputfile,'w') as fd:
    json.dump(results,fd)