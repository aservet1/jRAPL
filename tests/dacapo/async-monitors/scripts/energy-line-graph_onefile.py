#!/usr/bin/env python3

import json
import pandas as pd
import matplotlib.pyplot as plt
import os
from sys import argv

def diff_list(l):
    d = []
    for i in range(1,len(l)):
        d.append(l[i]-l[i-1])
    return d
def values_list(dictionary):
	return list(dictionary.values())
def filenamestub(path):
	return path.split('/')[-1].split('.')[0]

filename = argv[1]
if not filename.endswith('.csv'):
    print(argv[1],'must be csv file')
    exit(2)

df = pd.read_csv(filename)
del df['core'] #hardcoded delete core bc core doesnt work on jolteon

data = dict()
num_sockets = max(df['socket'])
for i in range(num_sockets): # filling out the socket level of result{}
    socket = i+1
    current = df[df.socket.eq(socket)].to_dict()
    del current['socket']
    data[socket] = current
    
for socket in data:
    for powd in data[socket]:
        #y = [ data[socket][powd][i] for i in data[socket][powd] ]
        y = values_list(data[socket][powd])
        y = diff_list(y)
        #x = list(range(1,len(y)+1))
        x = diff_list(values_list(data[socket]['timestamp']))
        plt.plot(x,y)
        print(len(x),len(y))

plt.savefig('EnergySeries_'+filenamestub(filename))
plt.clf()
