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

    # data = list()
    # for fname in filenames:
    #     with open(fname) as fh:
    #         data.append(json.loads(fh.read()))

        # fhs = [open(f) for f in filenames]
        # data = [json.loads(fh.read()) for fh in fhs]
        # data = [d for d in data if d['metadata']['monitor_type'] == monitor_type ]
        # for fh in fhs: fh.close()

    aggregated = {}

    aggregated['metadata'] = data[0]['metadata'] # copy over the first [metadata] block to keep common fields, over-write the aggregated fields
    for k in ['lifetime','numSamples']:
        dat = [ d['metadata'][k] for d in data ]
        aggregated['metadata'][k] = dict()
        aggregated['metadata'][k]['avg']   = statistics.mean(dat)  # = aggr_mean([1 for _ in range(len(metadata))],    [d['avg'] for d in metadata])
        aggregated['metadata'][k]['stdev'] = statistics.stdev(dat) # = aggr_stdev([1 for _ in range(len(metadata))], [d['stdev'] for d in metadata])
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
