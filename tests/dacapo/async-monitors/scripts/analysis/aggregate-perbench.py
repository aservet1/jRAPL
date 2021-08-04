#!/usr/bin/env python3
import os
import json
import math
import statistics
from sys import argv

from aggr_utils import *

input_extension = '.stats.json'
output_extension = '.aggregate-perbench.json'

if len(argv) != 2:
    print("usage: " + argv[0] + " directory with " + input_extension + " files to aggregate")
    exit(2)

data_directory = argv[1]
os.chdir(data_directory)
benchmarks    = sorted(list(set([ fname.split('.')[0].split('_')[0]  for fname in os.listdir() if fname.endswith(input_extension) ])))
monitor_types = sorted(list(set([ fname.split('.')[0].split('_')[-1] for fname in os.listdir() if fname.endswith(input_extension) ])))
if not len(monitor_types) and not len(benchmarks):
    print("no files found with extension",input_extension)
    exit(2)

for bench in benchmarks:
    for monitor_type in monitor_types:

        filenames = [ f for f in os.listdir() if (f.split('_')[0] == bench) and f.endswith(input_extension)]
        fhs = [ open(f) for f in filenames ]
        data = [ json.loads(fh.read()) for fh in fhs ]
        data = [ d for d in data if d['metadata']['monitor_type'] == monitor_type ]
        for fh in fhs: fh.close()

        # aggregate metadata (lifetime, numsamples. save monitor_type and benchmark), memory, and time-energy->{{powerdomain-energy},time}
        aggregated = {}

        aggregated['metadata'] = data[0]['metadata'] # copy over the first [metadata] block to keep common fields, over-write the aggregated fields

        aggregated['metadata']['lifetime_numSamples_covariance'] = covariance (
            [ d['metadata'][ 'lifetime' ] for d in data ],
            [ d['metadata']['numSamples'] for d in data ]
        )

        for k in ['lifetime','numSamples']:
            d = [ d['metadata'][k] for d in data ]
            aggregated['metadata'][k] = dict()
            aggregated['metadata'][k][ 'avg' ] = statistics.mean (d)
            aggregated['metadata'][k]['stdev'] = statistics.stdev(d)

        aggregated['metadata']['iteration'] = 'AGGREGATE_PERBENCH'
        aggregated['metadata']['benchmark'] = 'AGGREGATE_PERBENCH'

        aggregated['memory'] = {}
        aggregated['memory']['jraplon' ] = aggregate_memory_stats([ dat['memory']['jraplon']  for dat in data ])
        aggregated['memory']['jraploff'] = aggregate_memory_stats([ dat['memory']['jraploff'] for dat in data ])

        aggregated['time-energy'] = {}
        aggregated['time-energy']['time-between-samples'] = {}
        aggregated['time-energy'] = general_aggregate( [ dat['time-energy'] for dat in data ] )

        outfilename = aggregated['metadata']['benchmark'] + "_" + aggregated['metadata']['monitor_type'] + output_extension

        with open(outfilename,'w') as outfile:
            outfile.write(json.dumps(aggregated))
            print(" >> wrote to outfile: " + outfilename )



