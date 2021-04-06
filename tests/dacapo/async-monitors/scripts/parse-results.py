#!/usr/bin/env python3

import os
import sys
import json
import statistics
import pandas as pd
from collections import Counter
from statistics import StatisticsError

'''-----------------------------------------------------------------------------'''

def filter_zero_columns(dataframe): #delete columns that are 0.0 down the line. on jolteon, this happens for core
    for column in dataframe:
        if sum(dataframe[column]) == 0.0:
            del dataframe[column]

#def zero_intervals(l):
#    result = list()
#    z = 0
#    for i in range(1,len(l)):
#        if l[i]-l[i-1] == 0:
#            z += 1
#        else:
#            result.append(z)
#            z = 0
#    if z:
#        result.append(z)
#    return dict(Counter(result))

def diff_list(l):
    return [ float(float(l[i]) - float(l[i-1])) for i in range(1,len(l))]

def dict_to_list(d): #convert from dict<int,double> to list<double>
    return [ d[k] for k in d.keys() ]

def memory_data(benchmark, iteration, type):
    filename = '_'.join([benchmark, iteration, type]) + ".memory.json"
    with open(filename) as f: memdata = json.loads(f.read())
    samples = memdata['samples']
    del memdata['samples']
    memdata['max'] = min(samples)
    memdata['min'] = max(samples)
    memdata['avg'] = statistics.mean(samples)
    memdata['stdev'] = statistics.stdev(samples)
    memdata['median'] = statistics.median(samples)
    return memdata
'''-----------------------------------------------------------------------------'''

if len(sys.argv) != 2:
	print("usage: python3 "+sys.argv[0]+" <folder containing the data that you intend to process and generate results for>")
	exit(1)

results_dir = sys.argv[1]# 'jolteon-results-subset'
os.chdir(results_dir)

datafiles = os.listdir()
datafilenames = list(set([ name.split('.')[0] for name in datafiles])) #remove file extension

for filename in sorted([ f for f in datafilenames if not f.endswith("nojrapl")]): # potentially find a better way to gracefully deal with memory data files
    print("<=< started working on '"+filename+"'")
    filename_parts = filename.split('_')
    benchmark = filename_parts[0]
    iteration = filename_parts[1]
    monitor_type = filename_parts[2]

    with open(filename+'.metadata.json') as fh:
        metadata = json.loads(fh.read())
        metadata['benchmark'] = benchmark # add these next 3 items to the metadata
        metadata['iteration'] = iteration
        metadata['monitor_type'] = monitor_type

    result = {}
    result['metadata'] = metadata

    result['memory'] = dict()
    result['memory']['jraplon']  = memory_data(result['metadata']['benchmark'], result['metadata']['iteration'], result['metadata']['monitor_type'])
    result['memory']['jraploff'] = memory_data(result['metadata']['benchmark'], result['metadata']['iteration'], 'nojrapl')


    data = pd.read_csv(filename+'.csv')
    filter_zero_columns(data)

    result['persocket'] = dict()
    num_sockets = max(data['socket'])
    for i in range(num_sockets): # filling out the socket level of result{}
        socket = i+1
        current = data[data.socket.eq(socket)].to_dict()
        del current['socket']
        result['persocket'][socket] = current
    

    for socket in result['persocket']: # filling out the actual computations in result{}
        timestamps = dict_to_list(result['persocket'][socket]['timestamp'])
        del result['persocket'][socket]['timestamp']
        
        power_domains = list(result['persocket'][socket])
        for powd in power_domains:
            energy = result['persocket'][socket][powd]
            energy = dict_to_list(energy)
            energy = diff_list(energy)
            result['persocket'][socket][powd] = dict()
            result['persocket'][socket][powd]['energy-per-sample'] = dict()
            result['persocket'][socket][powd]['energy-per-sample']['num_samples'] = len(energy)
            result['persocket'][socket][powd]['energy-per-sample']['avg'] = statistics.mean(energy)
            result['persocket'][socket][powd]['energy-per-sample']['stdev'] = statistics.stdev(energy)

        time = diff_list(timestamps)
        result['persocket'][socket]['time-between-samples'] = {}
        result['persocket'][socket]['time-between-samples']['num_samples'] = len(time)
        result['persocket'][socket]['time-between-samples']['avg'] = statistics.mean(time)
        result['persocket'][socket]['time-between-samples']['stdev'] = statistics.stdev(time)
            
            
    with open(filename+'.stats.json','w') as fh:
        fh.write(json.dumps(result))

    result.clear()
    print(">=> done processing, json result written to "+filename+".stats.json'")
