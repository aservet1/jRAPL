#!/usr/bin/env python3

from sys import argv
import os
import json

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

            java_data = get_data_by_monitor_type(data, 'java')          ['time-energy']['energy-per-sample']
            c_ll_data = get_data_by_monitor_type(data, 'c-linklist')    ['time-energy']['energy-per-sample']
            c_da_data = get_data_by_monitor_type(data, 'c-dynamicarray')['time-energy']['energy-per-sample']

            result[benchmark][powd]                    =  {}
            result[benchmark][powd]['java']            =  {}
            result[benchmark][powd]['c-linklist']      =  {}
            result[benchmark][powd]['c-dynamicarray']  =  {}

            result[benchmark][powd]['java']          ['avg']   = java_data[powd]['avg']
            result[benchmark][powd]['c-linklist']    ['avg']   = c_ll_data[powd]['avg']
            result[benchmark][powd]['c-dynamicarray']['avg']   = c_da_data[powd]['avg']

            result[benchmark][powd]['java']          ['stdev'] = java_data[powd]['stdev']
            result[benchmark][powd]['c-linklist']    ['stdev'] = c_ll_data[powd]['stdev']
            result[benchmark][powd]['c-dynamicarray']['stdev'] = c_da_data[powd]['stdev']
    
    return result

def get_overall():
    data = load_data_by_file_extension('aggregate-permonitor.json', 'monitor_type')
    assert (
        len(data['java']) == 1
        and len(data['c-linklist']) == 1
        and len(data['c-dynamicarray']) == 1
    );

    java_data = data['java']          [0]['time-energy']['energy-per-sample']
    c_ll_data = data['c-linklist']    [0]['time-energy']['energy-per-sample']
    c_da_data = data['c-dynamicarray'][0]['time-energy']['energy-per-sample']

    result = {
        'per-socket': {},
        'combinded-socket': {}
    }

    power_domains = sorted (
        data['java'][0]['time-energy']['power-per-sample'].keys()
    ) # ['java'] and [0] are arbirary keys, powder_domains will be the same list regardless

    for powd in [p for p in power_domains if '_socket' in p]:
        result['per-socket'][powd] = {
            "java": {
                "avg"  :  java_data[powd]['avg'],
                "stdev":  java_data[powd]['stdev']
            },
            "c-linklist": {
                "avg"  :  c_ll_data[powd]['avg'],
                "stdev":  c_ll_data[powd]['stdev']
            },
            "c-dynamicarray": {
                "avg"  :  c_da_data[powd]['avg'],
                "stdev":  c_da_data[powd]['stdev']
            }
        }
    for powd in [p for p in power_domains if not '_socket' in p]:
        result['combined-socket'][powd] = {
            "java": {
                "avg"  :  java_data[powd]['avg'],
                "stdev":  java_data[powd]['stdev']
            },
            "c-linklist": {
                "avg"  :  c_ll_data[powd]['avg'],
                "stdev":  c_ll_data[powd]['stdev']
            },
            "c-dynamicarray": {
                "avg"  :  c_da_data[powd]['avg'],
                "stdev":  c_da_data[powd]['stdev']
            }
        }
    
    return result

'''------------------------------------------------------------------------------------'''

data_dir, result_dir = parse_cmdline_args(argv)
os.chdir(data_dir)
outputfile = os.path.join(result_dir,'energy-per-sample.json')

results = {}
results['overall']  = get_overall ()
results['perbench'] = get_perbench()

# results['plotinfo'] = {}
# results['plotinfo']['perbench'] = { 'filename': 'energy-per-sample_perbench', 'xlabel': 'Average Energy Per Sample (joules)' } 
# results['plotinfo']['overall']  = { 'filename': 'energy-per-sample_overall' , 'ylabel': 'Average Energy Per Sample (joules)' } 

with open(outputfile,'w') as fd:
    json.dump(results, fd)

print('wrote to',outputfile)