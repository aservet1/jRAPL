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

        java_metadata = get_by_monitor_type(data,'java')          ['metadata']
        c_ll_metadata = get_by_monitor_type(data,'c-linklist')    ['metadata']
        c_da_metadata = get_by_monitor_type(data,'c-dynamicarray')['metadata']

        result[benchmark]['java']           = {}
        result[benchmark]['c-linklist']     = {}
        result[benchmark]['c-dynamicarray'] = {}

        result[benchmark]['java']          ['avg'] = java_metadata['numSamples']['avg'] / java_metadata['lifetime']['avg']
        result[benchmark]['c-linklist']    ['avg'] = c_ll_metadata['numSamples']['avg'] / c_ll_metadata['lifetime']['avg']
        result[benchmark]['c-dynamicarray']['avg'] = c_da_metadata['numSamples']['avg'] / c_da_metadata['lifetime']['avg']

        # propagation-of-uncertainty division
        result[benchmark]['java']          ['stdev'] = math.sqrt( (java_metadata['numSamples']['stdev']/java_metadata['lifetime']['stdev'])**2 / java_metadata['lifetime']['stdev']**2 )
        result[benchmark]['c-linklist']    ['stdev'] = math.sqrt( (c_ll_metadata['numSamples']['stdev']/c_ll_metadata['lifetime']['stdev'])**2 / c_ll_metadata['lifetime']['stdev']**2 )
        result[benchmark]['c-dynamicarray']['stdev'] = math.sqrt( (c_da_metadata['numSamples']['stdev']/c_da_metadata['lifetime']['stdev'])**2 / c_da_metadata['lifetime']['stdev']**2 )

    return result

def get_overall():
    data = load_data_by_file_extension('aggregate-permonitor.json', 'monitor_type')
    assert(len(data['java']) == 1 and len(data['c-linklist']) == 1 and len(data['c-dynamicarray']) == 1)

    java_metadata = data['java'][0]['metadata']
    c_ll_metadata = data['c-linklist'][0]['metadata']
    c_da_metadata = data['c-dynamicarray'][0]['metadata']

    result = {}

    result['java']           = {}
    result['c-linklist']     = {}
    result['c-dynamicarray'] = {}

    result['java']          ['avg'] = java_metadata['numSamples']['avg'] / java_metadata['lifetime']['avg']
    result['c-linklist']    ['avg'] = c_ll_metadata['numSamples']['avg'] / c_ll_metadata['lifetime']['avg']
    result['c-dynamicarray']['avg'] = c_da_metadata['numSamples']['avg'] / c_da_metadata['lifetime']['avg']

    result['java']          ['stdev'] = math.sqrt(java_metadata['numSamples']['stdev']**2 / java_metadata['lifetime']['stdev']**2)
    result['c-linklist']    ['stdev'] = math.sqrt(c_ll_metadata['numSamples']['stdev']**2 / c_ll_metadata['lifetime']['stdev']**2)
    result['c-dynamicarray']['stdev'] = math.sqrt(c_da_metadata['numSamples']['stdev']**2 / c_da_metadata['lifetime']['stdev']**2)

    return result

'''---------------------------------------------------------------------------'''

data_dir, result_dir = parse_cmdline_args(argv)
os.chdir(data_dir)
outputfile = os.path.join(result_dir,'sampling-efficiency.json')

results = {}
results['perbench']  =  get_perbench ()
results['overall']   =  get_overall  ()

results['plotinfo'] = {}
results['plotinfo']['perbench'] = { 'filename': 'sampling-efficiency_perbench', 'xlabel': 'Sampling Efficiency', 'title': 'Sampling Efficiency' }
results['plotinfo']['overall']  = { 'filename': 'sampling-efficiency_overall' , 'ylabel': 'Sampling Efficiency', 'title': 'Sampling Efficiency' }

print('.) done with overall')

with open(outputfile,'w') as fd:
    json.dump(results,fd)

print('.) wrote to outfile: ' + outputfile)