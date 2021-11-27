#!/usr/bin/env python3

import pandas as pd
import matplotlib.pyplot as plt
from sys import argv

def diff_list(l,wraparound=0):
    d = []
    for i in range(1,len(l)):
        diff = l[i]-l[i-1]
        if diff < 0: diff += wraparound
        d.append(diff)
    return d

def filenamestub(path):
	return path.split('/')[-1].split('.')[0]

filename = argv[1]
if not filename.endswith('.csv'):
    print(argv[1],'must be csv file')
    exit(2)

df = pd.read_csv(filename)
del df['core'] #hardcoded delete core bc core doesnt work on jolteon
del df['socket'] #assume that socket has already been filtered before the file is passed in here

times = df['timestamp']
del df['timestamp']
for powd in df:
    energy = df[powd].to_list()
    x = times; y = energy
    plt.plot(x,y)
    plt.savefig('EnergySeries_'+powd+'_'+filenamestub(filename))
    plt.clf()
