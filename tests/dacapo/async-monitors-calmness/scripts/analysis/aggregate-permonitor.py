#!/usr/bin/env python3
import os
import json
import statistics
from sys import argv
from aggr_utils import *

'''-----------------------------------------------------------------'''

def extract_monitortype(filename):
    if '/' in filename: filename = filename.split('/')[-1]
    return filename.split('.')[0].split('_')[-1]

'''-----------------------------------------------------------------'''

input_extension  = '.stats.json'
output_extension = '.aggregate-permonitor.json'

try:
    data_directory = argv[1]
except:
    print("usage: " + argv[0] + " directory with " + input_extension + " files to aggregate")
    exit(2)

os.chdir(data_directory)

monitor_types = sorted(list(set([ extract_monitortype(fname) for fname in os.listdir() if fname.endswith(input_extension) ])))
if not len(monitor_types):
    print("no files found with extension",input_extension)
    exit(2)

for monitor_type in monitor_types:
    filenames = sorted([ fname for fname in os.listdir() if extract_monitortype(fname) == monitor_type and fname.endswith(input_extension) ])
    fds  = [ open(fname)   for fname in filenames ]
    data = [ json.load(fd) for fd    in fds       ]
    fds  = [ fd.close()    for fd    in fds       ]

    aggregated = {}

    aggregated['metadata'] = data[0]['metadata'] # copy over the first [metadata] block to keep common fields, over-write the aggregate fields

    sampling_rates = [ d['metadata']['samplingRate'] for d in data ]
    lifetimes = [ d['metadata']['lifetime'] for d in data ]
    sample_sizes = [ d['metadata']['numSamples'] for d in data ]
    assert(len(lifetimes)==len(sample_sizes)==len(sampling_rates)==len(data))
    normalized_sample_counts = [
        (sample_sizes[i] * sampling_rates[i]) / lifetimes[i]
        for i in range(len(data))
    ]

    aggregated['metadata']['lifetime_numSamples_covariance'] = covariance ( lifetimes, sample_sizes )

    aggregated['metadata']['lifetime'] = dict()
    aggregated['metadata']['lifetime']['avg']   = statistics.mean (lifetimes)
    aggregated['metadata']['lifetime']['stdev'] = statistics.stdev(lifetimes)

    aggregated['metadata']['numSamples'] = dict()
    aggregated['metadata']['numSamples']['avg']   = statistics.mean (sample_sizes)
    aggregated['metadata']['numSamples']['stdev'] = statistics.stdev(sample_sizes)

    aggregated['metadata']['normalizedSampleCount'] = dict()
    aggregated['metadata']['normalizedSampleCount']['avg']   = statistics.mean (normalized_sample_counts)
    aggregated['metadata']['normalizedSampleCount']['stdev'] = statistics.stdev(normalized_sample_counts)

    aggregated['metadata']['iteration'] = 'AGGREGATE_PERMONITOR'
    aggregated['metadata']['benchmark'] = 'AGGREGATE_PERMONITOR'

    aggregated['memory'] = {}
    aggregated['memory']['jraplon' ] = aggregate_memory_stats([ d['memory']['jraplon']  for d in data ])
    aggregated['memory']['jraploff'] = aggregate_memory_stats([ d['memory']['jraploff'] for d in data ])

    aggregated['time-energy'] = {}
    aggregated['time-energy']['time-between-samples'] = {}
    aggregated['time-energy'] = general_aggregate([ d['time-energy'] for d in data ])

    outfilename = aggregated['metadata']['monitor_type'] + output_extension
    with open(outfilename,'w') as fd:
        json.dump(aggregated,fd)
    print(" >> wrote to outfile: " + outfilename )
