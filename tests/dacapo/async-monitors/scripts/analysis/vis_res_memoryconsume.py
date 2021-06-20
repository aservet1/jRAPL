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

try:
	data_dir = argv[1]
	result_dir = argv[2]
except:
	print("usage:",argv[0],"<directory with all the .aggregate-stats.json files>","<directory to output the plots>")
	exit(2)

if not (result_dir.startswith("/") or result_dir.startswith("~")):
	result_dir = os.path.join(os.getcwd(),result_dir)
if not os.path.isdir(result_dir):
	print("directory",result_dir,"does not exist")
	exit(2)

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

for benchmark in sorted(data.keys()):
    print(' )> doing benchmark',benchmark)

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
        subtract_sd = sqrt((sa**2)+(sb**2))
        average = (a+b)/2
        average_sd = sqrt(.5**2 * sa**2 + .5**2 * sb**2)
        return sqrt( ((subtract/average)**2) * ( (subtract_sd/subtract)**2 + (average_sd/average)**2 ))


    labels.append(benchmark)

    java_avg.append( percent_diff(javg, nojavg) )
    java_std.append( percent_diff_stdev(jstd, nojstd, javg, nojavg) )
    java_numsamples.append( get_by_monitor_type(data, 'java')['metadata']['numSamples']['avg'] )

    c_ll_avg.append( percent_diff(clavg, nojavg) )
    c_ll_std.append( percent_diff_stdev(clstd, nojstd, clavg, nojavg) )
    c_ll_numsamples.append( get_by_monitor_type(data, 'c-linklist')['metadata']['numSamples']['avg'] )

    c_da_avg.append( percent_diff(cdavg, nojavg) )
    c_da_std.append( percent_diff_stdev(cdavg, nojavg, cdstd, nojstd) )
    c_da_numsamples.append( get_by_monitor_type(data, 'c-dynamicarray')['metadata']['numSamples']['avg'] )
    print(' <( done with benchmark',benchmark)

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

plt.savefig(os.path.join(result_dir,'memory-compare_perbenchmark'))
print(" <.> done making the per-benchmark graph")

## ---------------------------------------------------------------------------------------------------------- ##
## ---------------------------------------------------------------------------------------------------------- ##
## ---------------------------------------------------------------------------------------------------------- ##
## ---------------------------------------------------------------------------------------------------------- ##

## ---------- Now to average across all benchmarks and make a bar graph with error bars of the 3 ------------ ##

## ---------------------------------------------------------------------------------------------------------- ##
## ---------------------------------------------------------------------------------------------------------- ##
## ---------------------------------------------------------------------------------------------------------- ##
## ---------------------------------------------------------------------------------------------------------- ##

def aggr_mean(sample_sizes, averages):
    assert len(sample_sizes) == len(averages)
    return sum([ (sample_sizes[i]*averages[i]) for i in range(len(sample_sizes)) ]) / sum(sample_sizes)

def aggr_stdev(sample_sizes, stdevs):
    assert len(sample_sizes) == len(stdevs)
    return sqrt(sum([ (sample_sizes[i]*(stdevs[i]**2)) for i in range(len(sample_sizes)) ]) / sum (sample_sizes))

overall_java_avg = aggr_mean (java_numsamples, java_avg)
overall_java_std = aggr_stdev(java_numsamples, java_std)

overall_c_ll_avg = aggr_mean (c_ll_numsamples, c_ll_avg)
overall_c_ll_std = aggr_stdev(c_ll_numsamples, c_ll_std)

overall_c_da_avg = aggr_mean (c_da_numsamples, c_da_avg)
overall_c_da_std = aggr_stdev(c_da_numsamples, c_da_std)

labels = ['java','c-linklist','c-dynamicarray']

plt.clf()
plt.bar( \
	x = [0,1,2], \
    height = [overall_java_avg, overall_c_ll_avg, overall_c_da_avg], \
     yerr  = [overall_java_std, overall_c_ll_std, overall_c_da_std], \
    tick_label = labels, \
    capsize = .5 \
)

plt.xlabel('monitor type')
plt.ylabel('average memory percent difference')
plt.title('% memory difference that each type of monitor added')

fig = plt.gcf()
fig.set_size_inches(5,5)

plt.savefig(os.path.join(result_dir,'memory-compare_overall'))
print(" <.> done making the overall average graph")

with open(os.path.join(result_dir,'raw-overall-data.txt'),'w') as f:
	f.write("overall_java_avg: "+str(overall_java_avg)+"\n")
	f.write("overall_java_std: "+str(overall_java_std)+"\n")
	f.write("\n")
	f.write("overall_c_ll_avg: "+str(overall_c_ll_avg)+"\n")
	f.write("overall_c_ll_std: "+str(overall_c_ll_std)+"\n")
	f.write("\n")
	f.write("overall_c_da_avg: "+str(overall_c_da_avg)+"\n")
	f.write("overall_c_da_std: "+str(overall_c_da_std)+"\n")

print(" <.> done printing overall data")
