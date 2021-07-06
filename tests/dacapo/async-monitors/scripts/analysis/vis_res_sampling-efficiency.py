#!/usr/bin/env python3

from sys import argv
import os
import json
import pandas as pd
import statistics
import matplotlib.pyplot as plt
import matplotlib as mpl
import numpy as np
import math

from myutil import parse_cmdline_args

data_dir, result_dir = parse_cmdline_args(argv)
os.chdir(data_dir)

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

java_samples_per_ms_AVG = []
c_ll_samples_per_ms_AVG = []
c_da_samples_per_ms_AVG = []

java_samples_per_ms_STDEV = []
c_ll_samples_per_ms_STDEV = []
c_da_samples_per_ms_STDEV = []

for benchmark in data:

    def get_by_monitor_type(data, monitor_type):
        return [ d for d in data[benchmark] if d['metadata']['monitor_type'] == monitor_type ][0]

    labels.append(benchmark)

    java_metadata = get_by_monitor_type(data,'java')['metadata']
    c_ll_metadata = get_by_monitor_type(data,'c-linklist')['metadata']
    c_da_metadata = get_by_monitor_type(data,'c-dynamicarray')['metadata']

    java_samples_per_ms_AVG.append( java_metadata['numSamples']['avg'] / java_metadata['lifetime']['avg'] )
    c_ll_samples_per_ms_AVG.append( c_ll_metadata['numSamples']['avg'] / c_ll_metadata['lifetime']['avg'] )
    c_da_samples_per_ms_AVG.append( c_da_metadata['numSamples']['avg'] / c_da_metadata['lifetime']['avg'] )

    java_samples_per_ms_STDEV.append( math.sqrt( (java_metadata['numSamples']['stdev']**2) / (java_metadata['lifetime']['avg']**2) ) )
    c_ll_samples_per_ms_STDEV.append( math.sqrt( (c_ll_metadata['numSamples']['stdev']**2) / (c_ll_metadata['lifetime']['avg']**2) ) )
    c_da_samples_per_ms_STDEV.append( math.sqrt( (c_da_metadata['numSamples']['stdev']**2) / (c_da_metadata['lifetime']['avg']**2) ) )

## Make the all-benchmarks graph ##
bar_width = 0.25
mpl.rcParams['figure.dpi'] = 600
r1 = np.arange(len(labels))
r2 = [x + bar_width for x in r1]
r3 = [x + bar_width for x in r2]

plt.clf()
plt.barh(r1, java_samples_per_ms_AVG, bar_width, xerr=java_samples_per_ms_STDEV, color='#ffa600', edgecolor="white", label='Java')
plt.barh(r2, c_da_samples_per_ms_AVG, bar_width, xerr=c_da_samples_per_ms_STDEV, color='#003f5c', edgecolor="white", label='C Dynamic Array')
plt.barh(r3, c_ll_samples_per_ms_AVG, bar_width, xerr=c_ll_samples_per_ms_STDEV, color='#bc5090', edgecolor="white", label='C Linked List')

plt.ylabel('Benchmark', fontweight='bold')
plt.xlabel('samples per ms', fontweight='bold')
plt.yticks([r + bar_width for r in range(len(labels))], labels)
plt.legend()
fig = plt.gcf()
fig.set_size_inches(12,25)
#plt.show()
plt.savefig(os.path.join(result_dir,'sampling-efficiency_perbench'))

## Now to average across all benchmarks and make a bar graph with error bars of the 3 ##
''' https://math.stackexchange.com/questions/1547141/aggregating-standard-deviation-to-a-summary-point?fbclid=IwAR3GpT8cNoNbMHntA1dKhWKHGXvBj2W-t7NQU29qoqtsg37uZKZgkeDM-aE <-- formulas for aggr_mean and aggr_stdev '''
def aggr_mean(sample_sizes, averages):
    assert len(sample_sizes) == len(averages)
    return sum([ (sample_sizes[i]*averages[i]) for i in range(len(sample_sizes)) ]) / sum(sample_sizes)
def aggr_stdev(sample_sizes, stdevs):
    assert len(sample_sizes) == len(stdevs)
    return math.sqrt(sum([ (sample_sizes[i]*(stdevs[i]**2)) for i in range(len(sample_sizes)) ]) / sum (sample_sizes))

# TODO you should be doing these calculations in a separate 'overall aggregation' module.................
overall_java_avg = aggr_mean (,java_samples_per_ms_AVG)
overall_java_std = aggr_stdev(,java_samples_per_ms_STDEV)
overall_c_ll_avg = aggr_mean (,c_ll_samples_per_ms_AVG)
overall_c_ll_std = aggr_stdev(,c_ll_samples_per_ms_STDEV)
overall_c_da_avg = aggr_mean (,c_da_samples_per_ms_AVG)
overall_c_da_std = aggr_stdev(,c_da_samples_per_ms_STDEV)

labels = ['java','c-linklist','c-dynamicarray']

plt.clf()
plt.bar(x=[0,1,2], \
    height=[overall_java_avg, overall_c_ll_avg, overall_c_da_avg], \
    yerr=  [overall_java_std, overall_c_ll_std, overall_c_da_std], \
    tick_label=labels \
)

plt.xlabel('monitor type')
plt.ylabel('samples per ms')
plt.title('sampling efficiency of monitors (average across benchmarks)')

fig = plt.gcf()
fig.set_size_inches(5,5)

#plt.show()
plt.savefig( os.path.join(result_dir,'sampling-efficiency_overall') )

with open( os.path.join(result_dir,'sampling-efficiency_overall.raw'), 'w' ) as f:
	f.write('overall_java_avg: '+str(overall_java_avg)+"\n")
	f.write('overall_java_std: '+str(overall_java_std)+"\n")
	f.write("\n")
	f.write('overall_c_ll_avg: '+str(overall_c_ll_avg)+"\n")
	f.write('overall_c_ll_std: '+str(overall_c_ll_std)+"\n")
	f.write("\n")
	f.write('overall_c_da_avg: '+str(overall_c_da_avg)+"\n")
	f.write('overall_c_da_std: '+str(overall_c_da_std)+"\n")

