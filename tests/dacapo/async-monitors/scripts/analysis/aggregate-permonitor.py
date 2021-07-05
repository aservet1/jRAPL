#!/usr/bin/env python3
import os
import json
import math
import statistics
from sys import argv

from aggr_utils import *

# aggregate metadata (lifetime, numsamples. save monitor_type and benchmark), memory, and time-energy->{{powerdomain-energy},time}
def get_aggregated(data, label):
    aggregated = dict()
    aggregated['metadata'] = data[0]['metadata'] # copy over the first [metadata] block to keep common fields, over-write the aggregated fields
    for k in ['lifetime','numSamples']:
        metadata = [ d['metadata'][k] for d in data ]
        aggregated['metadata'][k] = dict()
        aggregated['metadata'][k]['avg'] = aggr_mean([1 for _ in range(len(metadata))],[d['avg'] for d in metadata])
        aggregated['metadata'][k]['stdev'] = aggr_stdev([1 for _ in range(len(metadata))],[d['stdev'] for d in metadata])
    aggregated['metadata']['iteration'] = label

    aggregated['memory'] = {}
    aggregated['memory']['jraplon' ] = aggregate_memory_stats([ d['memory']['jraplon']  for d in data ])
    aggregated['memory']['jraploff'] = aggregate_memory_stats([ d['memory']['jraploff'] for d in data ])

    aggregated['time-energy'] = {}
    aggregated['time-energy']['time-between-samples'] = {}
    aggregated['time-energy'] = general_aggregate([ d['time-energy'] for d in data ])

    return aggregated

def extract_monitortype(filename):
    if '/' in filename: filename = filename.split('/')[-1]
    return filename.split('.')[0].split('_')[-1]

'''-----------------------------------------------------------------'''

input_extension  = '.aggregate-perbench.json'
output_extension = '.aggregate-permonitor.json'

if len(argv) != 2:
    print("usage: " + argv[0] + " directory with " + input_extension + " files to aggregate")
    exit(2)

data_directory = argv[1]
os.chdir(data_directory)
monitor_types = sorted(list(set([ extract_monitortype(fname) for fname in os.listdir() if fname.endswith(input_extension) ])))
if not len(monitor_types):
    print("no files found with extension",input_extension)
    exit(2)

for monitor_type in monitor_types:
    filenames = sorted([ fname for fname in os.listdir() if extract_monitortype(fname) == monitor_type and fname.endswith(input_extension) ])
    
    data = list()
    for fname in filenames:
        with open(fname) as fh:
            data.append(json.loads(fh.read()))

        # fhs = [open(f) for f in filenames]
        # data = [json.loads(fh.read()) for fh in fhs]
        # data = [d for d in data if d['metadata']['monitor_type'] == monitor_type ]
        # for fh in fhs: fh.close()

    aggregated = get_aggregated(data, 'AGGREGATE_PERMONITOR')

    outfilename = aggregated['metadata']['monitor_type'] + output_extension
    with open(outfilename,'w') as outfile: outfile.write(json.dumps(aggregated))
    print(" >> wrote to outfile: " + outfilename )

