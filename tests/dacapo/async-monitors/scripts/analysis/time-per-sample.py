#!/usr/bin/env python3

import os
import math
import json
from sys import argv

from myutil import parse_cmdline_args, load_data_by_file_extension

'''---------------------------------------------------------------------------'''

def get_perbench():
    data = load_data_by_file_extension('aggregate-perbench.json', 'benchmark')

    result = {}

    for benchmark in data:
        result[benchmark] = {}

        def get_by_monitor_type(data, monitor_type):
            return [ d for d in data[benchmark] if d['metadata']['monitor_type'] == monitor_type ][0]

        java_data = get_by_monitor_type(data,'java')          ['time-energy']['time-between-samples']
        c_ll_data = get_by_monitor_type(data,'c-linklist')    ['time-energy']['time-between-samples']
        c_da_data = get_by_monitor_type(data,'c-dynamicarray')['time-energy']['time-between-samples']

        result[benchmark]['java']           = {}
        result[benchmark]['c-linklist']     = {}
        result[benchmark]['c-dynamicarray'] = {}

        # jsnprint(java_data)
        # print(benchmark)

        result[benchmark]['java']            ['avg']  = java_data['avg']
        result[benchmark]['c-linklist']      ['avg']  = c_ll_data['avg']
        result[benchmark]['c-dynamicarray']  ['avg']  = c_da_data['avg']

        result[benchmark]['java']           ['stdev'] = java_data['stdev']
        result[benchmark]['c-linklist']     ['stdev'] = c_ll_data['stdev']
        result[benchmark]['c-dynamicarray'] ['stdev'] = c_da_data['stdev']

    return result

def get_overall():
    data = load_data_by_file_extension('aggregate-permonitor.json', 'monitor_type')
    assert(len(data['java']) == 1 and len(data['c-linklist']) == 1 and len(data['c-dynamicarray']) == 1)

    java_data = data['java']          [0]['time-energy']['time-between-samples']
    c_ll_data = data['c-linklist']    [0]['time-energy']['time-between-samples']
    c_da_data = data['c-dynamicarray'][0]['time-energy']['time-between-samples']

    result = {}

    result['java']           = {}
    result['c-linklist']     = {}
    result['c-dynamicarray'] = {}

    result['java']            ['avg']  = java_data['avg']
    result['c-linklist']      ['avg']  = c_ll_data['avg']
    result['c-dynamicarray']  ['avg']  = c_da_data['avg']

    result['java']           ['stdev'] = java_data['stdev']
    result['c-linklist']     ['stdev'] = c_ll_data['stdev']
    result['c-dynamicarray'] ['stdev'] = c_da_data['stdev']

    return result

'''---------------------------------------------------------------------------'''

data_dir, result_dir = parse_cmdline_args(argv)
os.chdir(data_dir)
outputfile = os.path.join(result_dir,'time-per-sample.json')

results = {}
results['perbench']  =  get_perbench ()
results['overall']   =  get_overall  ()

results['plotinfo'] = {}
results['plotinfo']['perbench'] = { 'filename': 'time-per-sample_perbench', 'xlabel': 'Time Per Sample (usec)', 'title': 'Average Time Per Sample' }
results['plotinfo']['overall']  = { 'filename': 'time-per-sample_overall' , 'ylabel': 'Time Per Sample (usec)', 'title': 'Average Time Per Sample' }

print('.) done with overall')

with open(outputfile,'w') as fd:
    json.dump(results,fd)

print('.) wrote to outfile: ' + outputfile)