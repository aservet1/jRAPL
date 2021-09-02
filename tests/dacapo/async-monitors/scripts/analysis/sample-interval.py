#!/usr/bin/env python3

import os
import json
from sys import argv

from myutil import parse_cmdline_args, load_data_by_file_extension
from aggr_utils import divide_by_constant_propagate_uncertainty as div_uncertainty

'''---------------------------------------------------------------------------'''

def get_perbench():
    data = load_data_by_file_extension('aggregate-perbench.json', 'benchmark')

    result = {}
    result['observed'] = {}
    result['normalized'] = {}

    for benchmark in data:
        result['observed'][benchmark] = {}
        result['normalized'][benchmark] = {}

        def get_by_monitor_type(data, monitor_type):
            return [ d for d in data[benchmark] if d['metadata']['monitor_type'] == monitor_type ][0]

        java_data = get_by_monitor_type(data,'java')          ['time-energy']['time-between-samples']
        c_ll_data = get_by_monitor_type(data,'c-linklist')    ['time-energy']['time-between-samples']
        c_da_data = get_by_monitor_type(data,'c-dynamicarray')['time-energy']['time-between-samples']

        sampling_rate = get_by_monitor_type(data,'java')['metadata']['samplingRate'] # it's the same for all 3 of them
        msec_to_usec_conversion=1000

        result['observed'][benchmark]['java']           = {}
        result['observed'][benchmark]['c-linklist']     = {}
        result['observed'][benchmark]['c-dynamicarray'] = {}

        result['normalized'][benchmark]['java']           = {}
        result['normalized'][benchmark]['c-linklist']     = {}
        result['normalized'][benchmark]['c-dynamicarray'] = {}

        result['observed'][benchmark]['java']           ['avg']  = java_data['avg']
        result['observed'][benchmark]['c-linklist']     ['avg']  = c_ll_data['avg']
        result['observed'][benchmark]['c-dynamicarray'] ['avg']  = c_da_data['avg']
        result['observed'][benchmark]['java']          ['stdev'] = java_data['stdev']
        result['observed'][benchmark]['c-linklist']    ['stdev'] = c_ll_data['stdev']
        result['observed'][benchmark]['c-dynamicarray']['stdev'] = c_da_data['stdev']

        result['normalized'][benchmark]['java']           ['avg']  = java_data['avg']/ (sampling_rate*msec_to_usec_conversion)
        result['normalized'][benchmark]['c-linklist']     ['avg']  = c_ll_data['avg']/ (sampling_rate*msec_to_usec_conversion)
        result['normalized'][benchmark]['c-dynamicarray'] ['avg']  = c_da_data['avg']/ (sampling_rate*msec_to_usec_conversion)
        result['normalized'][benchmark]['java']          ['stdev'] = div_uncertainty(java_data['stdev'], sampling_rate*msec_to_usec_conversion)
        result['normalized'][benchmark]['c-linklist']    ['stdev'] = div_uncertainty(c_ll_data['stdev'], sampling_rate*msec_to_usec_conversion)
        result['normalized'][benchmark]['c-dynamicarray']['stdev'] = div_uncertainty(c_da_data['stdev'], sampling_rate*msec_to_usec_conversion)

    return result

def get_overall():
    data = load_data_by_file_extension('aggregate-permonitor.json', 'monitor_type')
    assert(len(data['java']) == 1 and len(data['c-linklist']) == 1 and len(data['c-dynamicarray']) == 1)

    java_data = data['java']          [0]['time-energy']['time-between-samples']
    c_ll_data = data['c-linklist']    [0]['time-energy']['time-between-samples']
    c_da_data = data['c-dynamicarray'][0]['time-energy']['time-between-samples']

    sampling_rate = data['java'][0]['metadata']['samplingRate'] # it's the same for all 3 monitors
    msec_to_usec_conversion=1000

    result = {}
    result['observed'] = {
        'java': {
            'avg': java_data['avg'],
            'stdev': java_data['stdev']
        },       
        'c-linklist': {
            'avg': c_ll_data['avg'],
            'stdev': c_ll_data['stdev']
        },
        'c-dynamicarray': {
            'avg': c_da_data['avg'],
            'stdev': c_da_data['stdev']
        }
    }
    result['normalized'] = {
        'java': {
            'avg': java_data['avg'] / (sampling_rate*msec_to_usec_conversion),
            'stdev': div_uncertainty(java_data['stdev'], sampling_rate*msec_to_usec_conversion)
        },       
        'c-linklist': {
            'avg': c_ll_data['avg'] / (sampling_rate*msec_to_usec_conversion),
            'stdev': div_uncertainty(c_ll_data['stdev'], sampling_rate*msec_to_usec_conversion)
        },
        'c-dynamicarray': {
            'avg': c_da_data['avg'] / (sampling_rate*msec_to_usec_conversion),
            'stdev': div_uncertainty(c_da_data['stdev'], sampling_rate*msec_to_usec_conversion)
        }
    }

    return result

'''---------------------------------------------------------------------------'''

data_dir, result_dir = parse_cmdline_args(argv)
os.chdir(data_dir)
outputfile = os.path.join(result_dir,'sample-interval.json')

results = {}
results['perbench']  =  get_perbench ()
results['overall']   =  get_overall  ()

# results['plotinfo'] = {}
# results['plotinfo']['perbench'] = { 'filename': 'time-per-sample_perbench', 'xlabel': 'Time Per Sample (usec)' }
# results['plotinfo']['overall']  = { 'filename': 'time-per-sample_overall' , 'ylabel': 'Time Per Sample (usec)' }

print('.) done with overall')

with open(outputfile,'w') as fd:
    json.dump(results,fd)

print('.) wrote to outfile: ' + outputfile)