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
    labels.append(benchmark)
    #d_ = data[benchmark]
    java_avg.append( [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'java' ][0]['memory']['jraplon']['avg'] )
    java_std.append( [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'java' ][0]['memory']['jraplon']['stdev'])

    c_ll_avg.append( [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-linklist' ][0]['memory']['jraplon']['avg'] )
    c_ll_std.append( [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-linklist' ][0]['memory']['jraplon']['stdev'] )

    c_da_avg.append( [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-dynamicarray' ][0]['memory']['jraplon']['avg'] )
    c_da_std.append( [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-dynamicarray' ][0]['memory']['jraplon']['stdev'] )

    # monitor_type is arbitrary for nojrapl, its the same for all of them, given a benchmark
    nojrapl_avg.append( [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-dynamicarray' ][0]['memory']['jraploff']['avg'] )
    nojrapl_std.append( [ d for d in data[benchmark] if d['metadata']['monitor_type'] == 'c-dynamicarray' ][0]['memory']['jraploff']['stdev'] )

bar_width = 0.25
mpl.rcParams['figure.dpi'] = 600
r1 = np.arange(len(c_ll_avg))
r2 = [x + bar_width for x in r1]
r3 = [x + bar_width for x in r2]
r4 = [x + bar_width for x in r3]
r5 = [x + bar_width for x in r4]
plt.barh(r1, c_da_avg,    bar_width, xerr=c_da_std,    color='#003f5c', edgecolor="white", label='C Dynamic Array')
plt.barh(r2, c_ll_avg,    bar_width, xerr=c_ll_std,    color='#bc5090', edgecolor="white", label='C Linked List')
plt.barh(r3, java_avg,    bar_width, xerr=java_std,    color='#ffa600', edgecolor="white", label='Java')
plt.barh(r4, nojrapl_avg, bar_width, xerr=nojrapl_std, color='#000000', edgecolor="white", label='No jRAPL')


plt.ylabel('Benchmark', fontweight='bold')
plt.xlabel('Average memory used (bytes)', fontweight='bold')
plt.yticks([r + bar_width for r in range(len(c_ll_avg))], labels)
plt.legend()
fig = plt.gcf()
fig.set_size_inches(12,25)
#plt.show()
plt.savefig('memory-comparison-bar')
