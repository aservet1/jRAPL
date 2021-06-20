#!/usr/bin/env python3

from sys import argv
import os
import json
import pandas as pd
import statistics
import matplotlib.pyplot as plt
import matplotlib as mpl
import numpy as np

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
print(json.dumps(data))

for powdomain in data['avrora'][0]['time-energy']['energy-per-sample'].keys(): # [avrora] and [0] are arbirary keys, powdomain will be the same list regardless
        print(powdomain)
        labels = []
        java_avg = []
        c_da_avg = []

        c_ll_avg = []
        java_std = []

        c_da_std = []
        c_ll_std = []

        for benchmark in data:
            print("  ",benchmark)
            
            labels.append(benchmark)

            def get_data_by_monitor_type(data, benchmark, type):
                return [ d for d in data[benchmark] if d['metadata']['monitor_type'] == type][0]['time-energy']['energy-per-sample']

            java_avg.append( get_data_by_monitor_type(data, benchmark, 'java' )[powdomain]['avg']   )
            java_std.append( get_data_by_monitor_type(data, benchmark, 'java' )[powdomain]['stdev'] )

            c_ll_avg.append( get_data_by_monitor_type(data, benchmark, 'c-linklist')[powdomain]['avg']   )
            c_ll_std.append( get_data_by_monitor_type(data, benchmark, 'c-linklist')[powdomain]['stdev'] )

            c_da_avg.append( get_data_by_monitor_type(data, benchmark, 'c-dynamicarray' )[powdomain]['avg']   )
            c_da_std.append( get_data_by_monitor_type(data, benchmark, 'c-dynamicarray' )[powdomain]['stdev'] )

        plt.clf()
        bar_width = 0.25
        mpl.rcParams['figure.dpi'] = 600
        r1 = np.arange(len(c_da_avg))
        r2 = [x + bar_width for x in r1]
        r3 = [x + bar_width for x in r2]
        r4 = [x + bar_width for x in r3]
        plt.barh(r1, c_da_avg,    bar_width, xerr=c_da_std,    color='#003f5c', edgecolor="white", label='C Dynamic Array')
        plt.barh(r2, c_ll_avg,    bar_width, xerr=c_ll_std,    color='#bc5090', edgecolor="white", label='C Linked List')
        plt.barh(r3, java_avg,    bar_width, xerr=java_std,    color='#ffa600', edgecolor="white", label='Java')

        plt.ylabel('Benchmark', fontweight='bold')
        plt.xlabel('Average energy per sample (bytes)', fontweight='bold')
        plt.yticks([r + bar_width for r in range(len(c_da_avg))], labels)
        plt.legend()
        fig = plt.gcf()
        fig.set_size_inches(12,25)
        #plt.show()
        plt.savefig(os.path.join(result_dir,'enerpersample-comparison_'+powdomain+'-bar'))
