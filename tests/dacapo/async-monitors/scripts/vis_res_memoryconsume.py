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
java_std = []

c_da_std = []
c_ll_std = []

nojrapl_avg = []
nojrapl_std = []

for benchmark in data:
    if benchmark == 'h2': continue

    javg = [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'java' ][0]['memory']['jraplon']['avg']
    jstd = [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'java' ][0]['memory']['jraplon']['stdev']

    clavg = [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-linklist' ][0]['memory']['jraplon']['avg'] 
    clstd = [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-linklist' ][0]['memory']['jraplon']['stdev']

    cdavg = [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-dynamicarray' ][0]['memory']['jraplon']['avg']  
    cdstd = [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-dynamicarray' ][0]['memory']['jraplon']['stdev']

    nojavg = [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-dynamicarray' ][0]['memory']['jraploff']['avg']
    nojstd = [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-dynamicarray' ][0]['memory']['jraploff']['stdev']

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

    c_ll_avg.append( percent_diff(clavg, nojavg) )
    c_ll_std.append( percent_diff_stdev(clstd, nojstd, clavg, nojavg) )

    c_da_avg.append( percent_diff(cdavg, nojavg) )
    c_da_std.append( percent_diff_stdev(cdavg, nojavg, cdstd, nojstd) )

    # monitor_type is arbitrary for nojrapl, its the same for all of them, given a benchmark
    #nojrapl_avg.append(  )
    #nojrapl_std.append(  )

bar_width = 0.25
mpl.rcParams['figure.dpi'] = 600
r1 = np.arange(len(c_ll_avg))
r2 = [x + bar_width for x in r1]
r3 = [x + bar_width for x in r2]
#r4 = [x + bar_width for x in r3]
plt.barh(r1, c_da_avg, bar_width, xerr=c_da_std, color='#003f5c', edgecolor="white", label='C Dynamic Array')
plt.barh(r2, c_ll_avg, bar_width, xerr=c_ll_std, color='#bc5090', edgecolor="white", label='C Linked List')
plt.barh(r3, java_avg, bar_width, xerr=java_std, color='#ffa600', edgecolor="white", label='Java')
#plt.barh(r4, nojrapl_avg, bar_width, xerr=nojrapl_std, color='#000000', edgecolor="white", label='No jRAPL')


plt.ylabel('Benchmark', fontweight='bold')
plt.xlabel('Percent Difference', fontweight='bold')
plt.yticks([r + bar_width for r in range(len(c_ll_avg))], labels)
plt.legend()
fig = plt.gcf()
fig.set_size_inches(12,25)
#plt.show()
plt.savefig('memory-comparison-bar')
