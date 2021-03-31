#!/usr/bin/env python3
import os
import json
import math
import statistics
from sys import argv

''' https://math.stackexchange.com/questions/1547141/aggregating-standard-deviation-to-a-summary-point?fbclid=IwAR3GpT8cNoNbMHntA1dKhWKHGXvBj2W-t7NQU29qoqtsg37uZKZgkeDM-aE <-- formulas for aggr_mean and aggr_stdev '''

def aggr_mean(sample_sizes, averages):
    assert len(sample_sizes) == len(averages)
    return sum([ (sample_sizes[i]*averages[i]) for i in range(len(sample_sizes)) ]) / sum(sample_sizes)

def aggr_stdev(sample_sizes, stdevs):
    assert len(sample_sizes) == len(stdevs)
    return math.sqrt(sum([ (sample_sizes[i]*(stdevs[i]**2)) for i in range(len(sample_sizes)) ]) / sum (sample_sizes))

def aggregate_memory_stats(memory_data):

    mem_stats = {}
    mem_stats['avg']   = aggr_mean([dat['num_samples'] for dat in memory_data], [dat['avg'] for dat in memory_data])
    mem_stats['stdev'] = aggr_stdev([dat['num_samples'] for dat in memory_data], [dat['avg'] for dat in memory_data])
    mem_stats['global_min'] = min( [ dat['min'] for dat in memory_data] )
    mem_stats['global_max'] = max( [ dat['max'] for dat in memory_data] )
    mem_stats['avg_min'] = statistics.mean( [ dat['min'] for dat in memory_data ] )
    mem_stats['avg_max'] = statistics.mean( [ dat['max'] for dat in memory_data ] )
    return mem_stats

"""
  Assumes 'data' is an array of structurally identical JSON object, where
  all of the leaf objects have keys ['avg','num_samples','stdev']. Recursively
  descends and then aggregates each of these three fields with the appropriate
  aggregation function.
"""
def general_aggregate(data):
	res = {}
	if sorted(list(data[0].keys())) != sorted(['avg','num_samples','stdev']):
		for k in data[0].keys():
			res[k] = general_aggregate( [ d[k] for d in data ] )
	else: # at the leaves
		sample_sizes = [ d['num_samples'] for d in data ]
		res['avg'] = aggr_mean( sample_sizes,  [d['avg'] for d in data ] )
		res['stdev'] = aggr_stdev( sample_sizes, [ d['stdev'] for d in data ] )
		res['num_samples'] = statistics.mean( [ d['num_samples'] for d in data ] )

	return res


if len(argv) != 2:
    print("usage: " + argv[0] + " directory with .stats.json files to aggregate")
    exit(2)

data_directory = argv[1]
os.chdir(data_directory)
benchmarks = list(set([ fname.split('_')[0] for fname in os.listdir() ]))

for bench in benchmarks:
    for monitor_type in ['c-linklist', 'c-dynamicarray', 'java']:
        filenames = [ f for f in os.listdir() if f.startswith(bench) and f.endswith('.stats.json')]
        fhs = [open(f) for f in filenames]
        data = [json.loads(fh.read()) for fh in fhs]
        data = [d for d in data if d['metadata']['monitor_type'] == monitor_type ]
        for fh in fhs: fh.close()

        # aggregate metadata (lifetime, numsamples. save monitor_type and benchmark), memory, and persocket->{{powerdomain-energy},time}
        aggregated = {}

        aggregated['metadata'] = data[0]['metadata'] # copy over the first [metadata] block to keep common fields, over-write the aggregated fields
        aggregated['metadata']['lifetime'] = statistics.mean([ dat['metadata']['lifetime'] for dat in data ])
        aggregated['metadata']['numSamples'] = statistics.mean([ dat['metadata']['num_samples'] for dat in data ])
        aggregated['metadata']['iteration'] = 'AGGREGATE'

        aggregated['memory'] = {}
        aggregated['memory']['jraplon' ] = aggregate_memory_stats([ dat['memory']['jraplon']  for dat in data ])
        aggregated['memory']['jraploff'] = aggregate_memory_stats([ dat['memory']['jraploff'] for dat in data ])

        aggregated['persocket'] = {}
        aggregated['persocket']['time-between-samples'] = {}
        aggregated['persocket'] = general_aggregate( [ dat['persocket'] for dat in data ] )

        outfilename = aggregated['metadata']['benchmark'] + "_" + aggregated['metadata']['monitor_type'] + ".aggregate-stats.json"
        with open(outfilename,'w') as outfile: outfile.write(json.dumps(aggregated))
        print(" >> wrote to outfile: " + outfilename)



