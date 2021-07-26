#!/usr/bin/env python3

from sys import argv
import os
import json

from aggr_utils import division_propagate_uncertainty
from myutil import parse_cmdline_args, load_data_by_file_extension

'''------------------------------------------------------------------------------------'''

def get_perbench():
    data = load_data_by_file_extension('aggregate-perbench.json', 'benchmark')
    result = {}

    benchmarks = sorted(data.keys())
    for benchmark in benchmarks:
        result[benchmark] = {}

    power_domains = sorted(data['avrora'][0]['time-energy']['energy-per-sample'].keys()) # [avrora] and [0] are arbirary keys, powdomain will be the same list regardless
    for powd in power_domains:

        for benchmark in benchmarks:
            def get_data_by_monitor_type(data, monitor_type):
                return [ d for d in data[benchmark] if d['metadata']['monitor_type'] == monitor_type ][0]

            java_energy = get_data_by_monitor_type(data, 'java')          ['time-energy']['energy-per-sample']
            c_ll_energy = get_data_by_monitor_type(data, 'c-linklist')    ['time-energy']['energy-per-sample']
            c_da_energy = get_data_by_monitor_type(data, 'c-dynamicarray')['time-energy']['energy-per-sample']

            java_time   = get_data_by_monitor_type(data, 'java')          ['time-energy']['time-between-samples']
            c_ll_time   = get_data_by_monitor_type(data, 'c-linklist')    ['time-energy']['time-between-samples']
            c_da_time   = get_data_by_monitor_type(data, 'c-dynamicarray')['time-energy']['time-between-samples']

            java_energy_avg   = java_energy[powd]['avg']
            c_ll_energy_avg   = c_ll_energy[powd]['avg']
            c_da_energy_avg   = c_da_energy[powd]['avg']

            java_energy_stdev = java_energy[powd]['stdev']
            c_ll_energy_stdev = c_ll_energy[powd]['stdev']
            c_da_energy_stdev = c_da_energy[powd]['stdev']

            java_time_avg     = java_time['avg']
            c_ll_time_avg     = c_ll_time['avg']
            c_da_time_avg     = c_da_time['avg']

            java_time_stdev   = java_time['stdev']
            c_ll_time_stdev   = c_ll_time['stdev']
            c_da_time_stdev   = c_da_time['stdev']


            result[benchmark][powd]                    =  {}
            result[benchmark][powd]['java']            =  {}
            result[benchmark][powd]['c-linklist']      =  {}
            result[benchmark][powd]['c-dynamicarray']  =  {}

            result[benchmark][powd]['java']          ['avg']   = java_energy_avg / java_time_avg
            result[benchmark][powd]['c-linklist']    ['avg']   = c_ll_energy_avg / c_ll_time_avg
            result[benchmark][powd]['c-dynamicarray']['avg']   = c_da_energy_avg / c_da_time_avg

            result[benchmark][powd]['java']          ['stdev'] = division_propagate_uncertainty(java_energy_stdev, java_time_stdev, java_energy_avg, java_time_avg)
            result[benchmark][powd]['c-linklist']    ['stdev'] = division_propagate_uncertainty(c_ll_energy_stdev, c_ll_time_stdev, c_ll_energy_avg, c_ll_time_avg)
            result[benchmark][powd]['c-dynamicarray']['stdev'] = division_propagate_uncertainty(c_da_energy_stdev, c_da_time_stdev, c_da_energy_avg, c_da_time_avg)
    
    return result

def get_overall():
    data = load_data_by_file_extension('aggregate-permonitor.json', 'monitor_type')
    assert(len(data['java']) == 1 and len(data['c-linklist']) == 1 and len(data['c-dynamicarray']) == 1)

    java_energy = data['java']          [0]['time-energy']['energy-per-sample']
    c_ll_energy = data['c-linklist']    [0]['time-energy']['energy-per-sample']
    c_da_energy = data['c-dynamicarray'][0]['time-energy']['energy-per-sample']

    java_time   = data['java']          [0]['time-energy']['time-between-samples']
    c_ll_time   = data['c-linklist']    [0]['time-energy']['time-between-samples']
    c_da_time   = data['c-dynamicarray'][0]['time-energy']['time-between-samples']

    result = {}

    power_domains = sorted(data['java'][0]['time-energy']['energy-per-sample'].keys()) # ['java'] and [0] are arbirary keys, powdomain will be the same list regardless
    for powd in power_domains:

        java_energy_avg   = java_energy[powd]['avg']
        c_ll_energy_avg   = c_ll_energy[powd]['avg']
        c_da_energy_avg   = c_da_energy[powd]['avg']

        java_energy_stdev = java_energy[powd]['stdev']
        c_ll_energy_stdev = c_ll_energy[powd]['stdev']
        c_da_energy_stdev = c_da_energy[powd]['stdev']

        java_time_avg     = java_time['avg']
        c_ll_time_avg     = c_ll_time['avg']
        c_da_time_avg     = c_da_time['avg']

        java_time_stdev   = java_time['stdev']
        c_ll_time_stdev   = c_ll_time['stdev']
        c_da_time_stdev   = c_da_time['stdev']
        
        result[powd]                   = {}
        result[powd]['java']           = {}
        result[powd]['c-linklist']     = {}
        result[powd]['c-dynamicarray'] = {}

        result[powd]['java']          ['avg']   = java_energy_avg / java_time_avg
        result[powd]['c-linklist']    ['avg']   = c_ll_energy_avg / c_ll_time_avg
        result[powd]['c-dynamicarray']['avg']   = c_da_energy_avg / c_da_time_avg

        result[powd]['java']          ['stdev'] = division_propagate_uncertainty(java_energy_stdev, java_time_stdev, java_energy_avg, java_time_avg)
        result[powd]['c-linklist']    ['stdev'] = division_propagate_uncertainty(c_ll_energy_stdev, c_ll_time_stdev, c_ll_energy_avg, c_ll_time_avg)
        result[powd]['c-dynamicarray']['stdev'] = division_propagate_uncertainty(c_da_energy_stdev, c_da_time_stdev, c_da_energy_avg, c_da_time_avg)

    return result

'''------------------------------------------------------------------------------------'''

data_dir, result_dir = parse_cmdline_args(argv)
os.chdir(data_dir)
outputfile = os.path.join(result_dir,'energy-per-sample.json')

results = {}
results['overall']  = get_overall ()
results['perbench'] = get_perbench()

results['plotinfo'] = {}
results['plotinfo']['perbench'] = { 'title': 'Energy Per Sample Normalized by Time Per Sample', 'filename': 'energy-per-sample_timenormalized_perbench', 'xlabel': 'Average Energy Per Sample (joules)' } 
results['plotinfo']['overall']  = { 'title': 'Energy Per Sample Normalized by Time Per Sample', 'filename': 'energy-per-sample_timenormalized_overall' , 'ylabel': 'Average Energy Per Sample (joules)' } 

with open(outputfile,'w') as fd:
    json.dump(results, fd)

print('wrote to',outputfile)
