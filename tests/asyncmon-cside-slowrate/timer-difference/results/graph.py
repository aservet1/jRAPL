#!/usr/bin/env python3

import os
import json
from sys import argv
import statistics
import matplotlib.pyplot as plt

if len(argv) != 2:
	print("usage: "+argv[0]+" <directory with result-n.json files>")
	exit(1)

os.chdir(argv[1])
filenames = [ f for f in os.listdir() if f.split('.')[-1] == 'json' ]
filenames

statparse = {}
for fname in filenames:
    with open(fname) as f: data = json.loads(f.read())
    duration = int(fname.split('.')[0].split('-')[-1])
    statparse[duration] = {}
    for kind in data.keys():
        dat = data[kind]
        statparse[duration][kind] = {}
        statparse[duration][kind]['avg'] = statistics.mean(dat)
        statparse[duration][kind]['std'] = statistics.stdev(dat)
        statparse[duration][kind]['raw'] = data[kind]

for duration in statparse:
    fig, ax = plt.subplots()
    
    j = statparse[duration]['java']
    c = statparse[duration]['c']
    
    kinds = ['java','c']
    CTEs = [j['avg'], c['avg']]
    error = [j['std'],c['std']]
    xpos = [x for x in range(len(kinds))]
    
    ax.bar(xpos, CTEs, yerr=error)
    ax.set_xticks(xpos)
    ax.set_xticklabels(['java','c'])
    ax.set_title("timer comparison for "+str(duration)+" msec")
    plt.savefig('time-comapre-'+str(duration))
    #plt.show()
    plt.clf()
