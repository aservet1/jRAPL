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

def diff_list(l, wraparound = 0):
	diffs = []
	for i in range(1,len(l)):
		diff = l[i] - l[i-1]
		#if diff < 0:
		#	print(".> caught negative diff:",diff,l[i],l[i-1])
		#	diff += wraparound
		diffs.append(diff)
	return diffs
    # return [ float(float(l[i]) - float(l[i-1])) for i in range(1,len(l))]

def memory_data(benchmark, iteration, type):
    filename = '_'.join([benchmark, iteration, type]) + ".memory.json"
    with open(filename) as f: memdata = json.loads(f.read())
    samples = memdata['samples']
    del memdata['samples']
    ##.#.## -- Do not delete these! We will probably end up not includling these metrics, but we might!! Do not delete them unless they are confirmed useless!
    ##.#.##memdata['max'] = min(samples)
    ##.#.##memdata['min'] = max(samples)
    ##.#.##memdata['median'] = statistics.median(samples)
    memdata['avg'] = statistics.mean(samples)
    memdata['stdev'] = statistics.stdev(samples)
    del memdata['timestamps'] # this is useful for generating an individual time-series plot of the memory of an iteration. but not for here.
    return memdata
'''-----------------------------------------------------------------------------'''

if len(sys.argv) != 2:
	print("usage: python3 "+sys.argv[0]+" <folder containing the data that you intend to process and generate results for>")
	exit(1)

results_dir = sys.argv[1]# 'jolteon-results-subset'
os.chdir(results_dir)

datafiles = os.listdir()
datafilenames = list(set([ name.split('.')[0] for name in datafiles ])) #remove file extension

for filename in sorted([ f for f in datafilenames if not f.endswith("nojrapl")]): # potentially find a better way to gracefully deal with memory data files
    filename_parts = filename.split('_')
    if len(filename_parts) != 3: continue
    print("<=< started working on '"+filename+"'")
    benchmark = filename_parts[0]
    iteration = filename_parts[1]
    monitor_type = filename_parts[2]

    result = {}

    with open(filename+'.metadata.json') as fh:
        metadata = json.loads(fh.read())
        metadata['benchmark'] = benchmark # add these next 3 items to the metadata
        metadata['iteration'] = iteration
        metadata['monitor_type'] = monitor_type
        result['metadata'] = metadata

    result['memory'] = dict()
    result['memory']['jraplon']  = memory_data(result['metadata']['benchmark'], result['metadata']['iteration'], result['metadata']['monitor_type'])
    result['memory']['jraploff'] = memory_data(result['metadata']['benchmark'], result['metadata']['iteration'], 'nojrapl')

    energydata = pd.read_csv(filename+'.csv')
    filter_zero_columns(energydata)

    result['time-energy'] = dict()

    timestamps = energydata['timestamp'].to_list()
    del energydata['timestamp']

    result['time-energy']['energy-per-sample'] = dict()
    power_domains = list(energydata.keys())
    for powd in power_domains:
        energy = diff_list (
            energydata[powd].to_list(),
            wraparound = metadata['energyWrapAround']
        )
        result['time-energy']['energy-per-sample'][powd] = dict()
        result['time-energy']['energy-per-sample'][powd]['num_samples'] = len(energy)
        result['time-energy']['energy-per-sample'][powd]['avg'] = statistics.mean(energy)
        result['time-energy']['energy-per-sample'][powd]['stdev'] = statistics.stdev(energy)

    time = diff_list(timestamps)
    result['time-energy']['time-between-samples'] = {}
    result['time-energy']['time-between-samples']['num_samples'] = len(time)
    result['time-energy']['time-between-samples']['avg'] = statistics.mean(time)
    result['time-energy']['time-between-samples']['stdev'] = statistics.stdev(time)
    
    # TODO: power-per-sample (can parallel divide the arrays of energy and timestamps, then convert to Watts from joules/usec
            
    with open(filename+'.stats.json','w') as fh:
        fh.write(json.dumps(result))

    result.clear()
    print(">=> done processing, json result written to "+filename+".stats.json'")
