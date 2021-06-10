#!/usr/bin/env python3
# To add a new cell, type '# %%'
# To add a new markdown cell, type '# %% [markdown]'
# %%
import json
import pandas as pd
import matplotlib.pyplot as plt
import os
from sys import argv

# %%
path = argv[1]#"/home/alejandro/jRAPL/tests/dacapo/async-monitors/jolteon-results-subset"
os.chdir(path)
files = []
for file in os.listdir():
    if file.endswith(".csv"):
        files.append(file)

def diff_list(l):
    d = []
    for i in range(1,len(l)):
        d.append(l[i]-l[i-1])
    return d


# %%
for file in sorted(files):
    print(file)
    df = pd.read_csv(file)
    del df['timestamp']; del df['core'] #hardcoded delete core bc core doesnt work on jolteon

    data = dict()
    num_sockets = max(df['socket'])
    for i in range(num_sockets): # filling out the socket level of result{}
        socket = i+1
        current = df[df.socket.eq(socket)].to_dict()
        del current['socket']
        data[socket] = current
        
    for socket in data:
        for powd in data[socket]:
            y = [ data[socket][powd][i] for i in data[socket][powd] ]
            y = diff_list(y)
            x = list(range(1,len(y)+1))
            plt.plot(x,y)
        plt.savefig('EnergySeries_socket'+str(socket)+'_'+file.split('.')[0])
        plt.clf()
