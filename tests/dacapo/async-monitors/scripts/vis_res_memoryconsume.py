#!/usr/bin/env python3

from sys import argv

if len(argv) != 2:
    print("provide target directory as command line argument")
    exit(2)

import os
import json
import pandas as pd
import statistics
import matplotlib.pyplot as plt
import matplotlib as mpl
import numpy as np
import math

targetDir = argv[1]#'/home/alejandro/jRAPL/tests/dacapo/async-monitors/jolteon-results-subset'
os.chdir(targetDir)
files = sorted([ f for f in os.listdir() if f.endswith('.aggregate-stats.json') ])

data = []
for fname in files:
    with open(fname) as f:
        d = json.loads(f.read())
        data.append(d)
x = {}
for d in data:
    bench = d['metadata']['benchmark']
    if not bench in x.keys(): x[bench] = [d]
    else: x[bench].append(d)
data = x

labels = []

java_avg = []
c_da_avg = []
c_ll_avg = []
nojrapl_avg = []

java_std = []
c_da_std = []
c_ll_std = []
nojrapl_std = []

java_numsamples = []
c_ll_numsamples = []
c_da_numsamples = []

for benchmark in data:
    if benchmark == 'h2': continue

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


    def percent_diff(a,b):
        return (a-b)/((a+b)/2)

    def percent_diff_stdev(sa,sb,a,b): # uses propagation of error through the percent diff
        subtract = a-b
        subtract_sd = math.sqrt((sa**2)+(sb**2))
        average = (a+b)/2
        average_sd = math.sqrt(.5**2 * sa**2 + .5**2 * sb**2)
        return math.sqrt( ((subtract/average)**2) * ( (subtract_sd/subtract)**2 + (average_sd/average)**2 ))


    labels.append(benchmark)

    java_avg.append( percent_diff(javg, nojavg) )
    java_std.append( percent_diff_stdev(jstd, nojstd, javg, nojavg) )
    java_numsamples.append( get_by_monitor_type(data, 'java')['metadata']['numSamples'] )

    c_ll_avg.append( percent_diff(clavg, nojavg) )
    c_ll_std.append( percent_diff_stdev(clstd, nojstd, clavg, nojavg) )
    c_ll_numsamples.append( get_by_monitor_type(data, 'c-linklist')['metadata']['numSamples'] )

    c_da_avg.append( percent_diff(cdavg, nojavg) )
    c_da_std.append( percent_diff_stdev(cdavg, nojavg, cdstd, nojstd) )
    c_da_numsamples.append( get_by_monitor_type(data, 'c-dynamicarray')['metadata']['numSamples'] )

## Make the all-benchmarks graph ##
bar_width = 0.25
mpl.rcParams['figure.dpi'] = 600
r1 = np.arange(len(c_ll_avg))
r2 = [x + bar_width for x in r1]
r3 = [x + bar_width for x in r2]

plt.clf()
plt.barh(r1, c_da_avg, bar_width, xerr=c_da_std, color='#003f5c', edgecolor="white", label='C Dynamic Array')
plt.barh(r2, c_ll_avg, bar_width, xerr=c_ll_std, color='#bc5090', edgecolor="white", label='C Linked List')
plt.barh(r3, java_avg, bar_width, xerr=java_std, color='#ffa600', edgecolor="white", label='Java')

plt.ylabel('Benchmark', fontweight='bold')
plt.xlabel('Percent Difference', fontweight='bold')
plt.yticks([r + bar_width for r in range(len(c_ll_avg))], labels)
plt.legend()
fig = plt.gcf()
fig.set_size_inches(12,25)
#plt.show()
plt.savefig('memory-compare_perbenchmark')

## Now to average across all benchmarks and make a bar graph with error bars of the 3 ##
import math

def aggr_mean(sample_sizes, averages):
    assert len(sample_sizes) == len(averages)
    return sum([ (sample_sizes[i]*averages[i]) for i in range(len(sample_sizes)) ]) / sum(sample_sizes)

def aggr_stdev(sample_sizes, stdevs):
    assert len(sample_sizes) == len(stdevs)
    return math.sqrt(sum([ (sample_sizes[i]*(stdevs[i]**2)) for i in range(len(sample_sizes)) ]) / sum (sample_sizes))

overall_java_avg = aggr_mean (java_numsamples, java_avg)
overall_java_std = aggr_stdev(java_numsamples, java_std)

overall_c_ll_avg = aggr_mean (c_ll_numsamples, c_ll_avg)
overall_c_ll_std = aggr_stdev(c_ll_numsamples, c_ll_std)

overall_c_da_avg = aggr_mean (c_da_numsamples, c_da_avg)
overall_c_da_std = aggr_stdev(c_da_numsamples, c_da_std)

labels = ['java','c-linklist','c-dynamicarray']

plt.clf()
plt.bar(x=[0,1,2], \
    height=[overall_java_avg, overall_c_ll_avg, overall_c_da_avg], \
    yerr=[overall_java_std, overall_c_ll_std, overall_c_da_std], \
    tick_label=labels \
)

plt.xlabel('monitor type')
plt.ylabel('average memory percent difference')
plt.title('% memory difference that each type of monitor added')

fig = plt.gcf()
fig.set_size_inches(5,5)

#plt.show()
plt.savefig('memory-compare_overall')

