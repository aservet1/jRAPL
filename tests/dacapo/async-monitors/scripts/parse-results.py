#!/usr/bin/env python3

import os
import sys
import json
import statistics
import pandas as pd
from collections import Counter
from statistics import StatisticsError

'''------------------------------------------------------------------------------------------'''

def filter_zero_columns(dataframe): #delete columnds that are 0.0 down the entire line
    for column in dataframe:
        if sum(dataframe[column]) == 0.0:
            del dataframe[column]

def zero_intervals(l):
    result = list()
    z = 0
    for i in range(1,len(l)):
        if l[i]-l[i-1] == 0:
            z += 1
        else:
            result.append(z)
            z = 0
    if z:
        result.append(z)
    return dict(Counter(result))

def diff_list(l):
    return [ float(float(l[i]) - float(l[i-1])) for i in range(1,len(l))]
def avg_nonzero_energy_increase(energy):
    try:
    	return statistics.mean([ n for n in diff_list(energy) if n != 0])
    except StatisticsError:
        return 0
def stdev_nonzero_energy_increase(energy):
    try:
        return statistics.stdev([ n for n in diff_list(energy) if n != 0 ])
    except StatisticsError:
        return 0

'''------------------------------------------------------------------------------------------'''

if len(sys.argv) != 2:
	print("usage: python3 "+sys.argv[0]+" <folder containing the data that you intend to process and generate results for>")
	exit(1)

results_dir = sys.argv[1]# 'jolteon-results-subset'
os.chdir(results_dir)

datafiles = os.listdir()
datafilenames = list(set([ name.split('.')[0] for name in datafiles]))

for filename in sorted(datafilenames):
    print("<=< started working on '"+filename+"'")
    # parts = filename.split('_')
    # benchmark = parts[0]
    # iter = parts[1]
    # type = parts[2]

    with open(filename+'.metadata.json') as fh:
        metadata = json.loads(fh.read())

    data = pd.read_csv(filename+'.csv')
    filter_zero_columns(data)

    result = {}

    result['metadata'] = metadata

    result['persocket'] = dict()
    num_sockets = max(data['socket'])
    for i in range(num_sockets): # filling out the socket level of result{}
        socket = i+1
        current = data[data.socket.eq(socket)].to_dict()
        del current['socket']
        result['persocket'][socket] = current
    

    for socket in result['persocket']: # filling out the actual computations in result{}
        timestamps = result['persocket'][socket]['timestamp']
        del result['persocket'][socket]['timestamp']
        power_domains = list(result['persocket'][socket])
        for powd in power_domains:
            energy = result['persocket'][socket][powd]
            energy = [ energy[i] for i in energy ] #convert from dict<int,double> to list<double>
            result['persocket'][socket][powd] = dict()
            result['persocket'][socket][powd]['zero-intervals'] = zero_intervals(energy)
            result['persocket'][socket][powd]['nonzero-energy-increase'] = dict()
            result['persocket'][socket][powd]['nonzero-energy-increase']['avg'] = avg_nonzero_energy_increase(energy)
            result['persocket'][socket][powd]['nonzero-energy-increase']['stdev'] = stdev_nonzero_energy_increase(energy)

    with open(filename+'.stats.json','w') as fh:
        fh.write(json.dumps(result))

    result.clear()
    print(">=> done processing, json result written to "+filename+".stats.json'")
