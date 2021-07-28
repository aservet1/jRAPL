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
	json_data_file = argv[1]
	result_dir = argv[2]
except:
	print("usage:",argv[0],"<json data file>","<directory to output the plots>")
	exit(2)

if not (result_dir.startswith("/") or result_dir.startswith("~")):
	result_dir = os.path.join(os.getcwd(),result_dir)
if not os.path.isdir(result_dir):
	print("directory",result_dir,"does not exist")
	exit(2)

with open(json_data_file) as fd:
    data = json.loads(fd.read())

labels = []
java_avg = []; c_da_avg = []; c_ll_avg = [];
java_std = []; c_da_std = []; c_ll_std = [];


for benchmark in sorted(data['perbench'].keys()):

    java_avg.append(data['perbench'][benchmark]['java']['avg']  )
    java_std.append(data['perbench'][benchmark]['java']['stdev'])

    c_ll_avg.append(data['perbench'][benchmark]['c-linklist']['avg'])
    c_ll_std.append(data['perbench'][benchmark]['c-linklist']['stdev'])

    c_da_avg.append(data['perbench'][benchmark]['c-dynamicarray']['avg'])
    c_da_std.append(data['perbench'][benchmark]['c-dynamicarray']['stdev'])

    labels.append(benchmark)

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

overall_java_avg = data['overall']['java']['avg'] 
overall_java_std = data['overall']['java']['stdev']

overall_c_ll_avg = data['overall']['c-linklist']['avg'] 
overall_c_ll_std = data['overall']['c-linklist']['stdev']

overall_c_da_avg = data['overall']['c-dynamicarray']['avg'] 
overall_c_da_std = data['overall']['c-dynamicarray']['stdev']

labels = ['java','c-linklist','c-dynamicarray']

plt.clf()
plt.bar (                                                                   \
	x = [0,1,2],                                                            \
    height     = [overall_java_avg, overall_c_ll_avg, overall_c_da_avg],    \
    yerr       = [overall_java_std, overall_c_ll_std, overall_c_da_std],    \
    tick_label = labels,                                                    \
    capsize    = .5                                                         \
)

plt.xlabel('monitor type')
plt.ylabel('average memory percent difference')

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
