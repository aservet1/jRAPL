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

targetDir = argv[1]
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

java_samples_per_ms = []
c_ll_samples_per_ms = []
c_da_samples_per_ms = []

for benchmark in data:
    if benchmark == 'h2': continue

    def get_by_monitor_type(data, monitor_type):
        return [ d for d in data[benchmark] if d['metadata']['monitor_type'] == monitor_type ][0]

    labels.append(benchmark)

    java_metadata = get_by_monitor_type(data,'java')['metadata']
    c_ll_metadata = get_by_monitor_type(data,'c-linklist')['metadata']
    c_da_metadata = get_by_monitor_type(data,'c-dynamicarray')['metadata']

    java_samples_per_ms.append( java_metadata['numSamples'] / java_metadata['lifetime'] )
    c_ll_samples_per_ms.append( (c_ll_metadata['numSamples']/2) / c_ll_metadata['lifetime'] ) # MAKE SURE YOU KNOW WHEN THE /2 IS APPROPRIATE (when you re-run the tests, get rid of this, since the bug in the source code has been fixed)
    c_da_samples_per_ms.append( (c_da_metadata['numSamples']/2) / c_da_metadata['lifetime'] )

## Make the all-benchmarks graph ##
bar_width = 0.25
mpl.rcParams['figure.dpi'] = 600
r1 = np.arange(len(labels))
r2 = [x + bar_width for x in r1]
r3 = [x + bar_width for x in r2]

plt.clf()
plt.barh(r1, java_samples_per_ms, bar_width, color='#ffa600', edgecolor="white", label='Java')
plt.barh(r2, c_da_samples_per_ms, bar_width, color='#003f5c', edgecolor="white", label='C Dynamic Array')
plt.barh(r3, c_ll_samples_per_ms, bar_width, color='#bc5090', edgecolor="white", label='C Linked List')

plt.ylabel('Benchmark', fontweight='bold')
plt.xlabel('samples per ms', fontweight='bold')
plt.yticks([r + bar_width for r in range(len(labels))], labels)
plt.legend()
fig = plt.gcf()
fig.set_size_inches(12,25)
#plt.show()
plt.savefig('sampling-efficiency_perbench')

## Now to average across all benchmarks and make a bar graph with error bars of the 3 ##

overall_java_avg = statistics.mean (java_samples_per_ms)
#overall_java_std = statistics.stdev()

overall_c_ll_avg = statistics.mean (c_ll_samples_per_ms)
#overall_c_ll_std = statistics.stdev()

overall_c_da_avg = statistics.mean (c_da_samples_per_ms)
#overall_c_da_std = statistics.stdev()

labels = ['java','c-linklist','c-dynamicarray']

plt.clf()
plt.bar(x=[0,1,2], \
    height=[overall_java_avg, overall_c_ll_avg, overall_c_da_avg], \
    #yerr=[overall_java_std, overall_c_ll_std, overall_c_da_std], \
    tick_label=labels \
)

plt.xlabel('monitor type')
plt.ylabel('samples per ms')
plt.title('sampling efficiency of monitors (average across benchmarks)')

fig = plt.gcf()
fig.set_size_inches(5,5)

#plt.show()
plt.savefig('sampling-efficiency_overall')

