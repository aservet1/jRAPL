#!/usr/bin/env python3

import os
import math
import json
from sys import argv

from myutil import parse_cmdline_args, load_data_by_file_extension
from aggr_utils import \
	division_propagate_uncertainty as div_uncertainty#, \
	#multiply_by_constant_propagate_uncertainty as mult_uncertainty

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

        java_NS_avg    =  java_metadata['numSamples']['avg']
        c_ll_NS_avg    =  c_ll_metadata['numSamples']['avg']
        c_da_NS_avg    =  c_da_metadata['numSamples']['avg']
        java_NS_stdev  =  java_metadata['numSamples']['stdev']
        c_ll_NS_stdev  =  c_ll_metadata['numSamples']['stdev']
        c_da_NS_stdev  =  c_da_metadata['numSamples']['stdev']

        java_LI_avg    =  java_metadata['lifetime']['avg']
        c_ll_LI_avg    =  c_ll_metadata['lifetime']['avg']
        c_da_LI_avg    =  c_da_metadata['lifetime']['avg']
        java_LI_stdev  =  java_metadata['lifetime']['stdev']
        c_ll_LI_stdev  =  c_ll_metadata['lifetime']['stdev']
        c_da_LI_stdev  =  c_da_metadata['lifetime']['stdev']

        result[benchmark]['java']           ['avg']  = java_NS_avg / java_LI_avg
        result[benchmark]['c-linklist']     ['avg']  = c_ll_NS_avg / c_ll_LI_avg
        result[benchmark]['c-dynamicarray'] ['avg']  = c_da_NS_avg / c_da_LI_avg

        result[benchmark]['java']          ['stdev'] = div_uncertainty(java_NS_stdev, java_LI_stdev, java_NS_avg, java_LI_avg)
        result[benchmark]['c-linklist']    ['stdev'] = div_uncertainty(c_ll_NS_stdev, c_ll_LI_stdev, c_ll_NS_avg, c_ll_LI_avg)
        result[benchmark]['c-dynamicarray']['stdev'] = div_uncertainty(c_da_NS_stdev, c_da_LI_stdev, c_da_NS_avg, c_da_LI_avg)

    return result

# because of stuff and technicalities and things, i calculated all of the permonitor results directly in my aggregate-permonitor one, so this is just parsing those results out
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

    result['java']           ['avg']  = java_metadata['samplingEfficiency']['avg']
    result['c-linklist']     ['avg']  = c_ll_metadata['samplingEfficiency']['avg']
    result['c-dynamicarray'] ['avg']  = c_da_metadata['samplingEfficiency']['avg']

    result['java']          ['stdev'] = java_metadata['samplingEfficiency']['stdev']
    result['c-linklist']    ['stdev'] = c_ll_metadata['samplingEfficiency']['stdev']
    result['c-dynamicarray']['stdev'] = c_da_metadata['samplingEfficiency']['stdev']

    return result

    # result = {}

    # result['java']           = {}
    # result['c-linklist']     = {}
    # result['c-dynamicarray'] = {}

    # java_NS_avg    =  java_metadata['numSamples']['avg']
    # c_ll_NS_avg    =  c_ll_metadata['numSamples']['avg']
    # c_da_NS_avg    =  c_da_metadata['numSamples']['avg']
    # java_NS_stdev  =  java_metadata['numSamples']['stdev']
    # c_ll_NS_stdev  =  c_ll_metadata['numSamples']['stdev']
    # c_da_NS_stdev  =  c_da_metadata['numSamples']['stdev']

    # java_LI_avg    =  java_metadata['lifetime']['avg']
    # c_ll_LI_avg    =  c_ll_metadata['lifetime']['avg']
    # c_da_LI_avg    =  c_da_metadata['lifetime']['avg']
    # java_LI_stdev  =  java_metadata['lifetime']['stdev']
    # c_ll_LI_stdev  =  c_ll_metadata['lifetime']['stdev']
    # c_da_LI_stdev  =  c_da_metadata['lifetime']['stdev']

    # result['java']           ['avg']  = java_NS_avg / java_LI_avg
    # result['c-linklist']     ['avg']  = c_ll_NS_avg / c_ll_LI_avg
    # result['c-dynamicarray'] ['avg']  = c_da_NS_avg / c_da_LI_avg

    # result['java']          ['stdev'] = div_uncertainty(java_NS_stdev, java_LI_stdev, java_NS_avg, java_LI_avg)
    # result['c-linklist']    ['stdev'] = div_uncertainty(c_ll_NS_stdev, c_ll_LI_stdev, c_ll_NS_avg, c_ll_LI_avg)
    # result['c-dynamicarray']['stdev'] = div_uncertainty(c_da_NS_stdev, c_da_LI_stdev, c_da_NS_avg, c_da_LI_avg)

    # return result

'''---------------------------------------------------------------------------'''

data_dir, result_dir = parse_cmdline_args(argv)
os.chdir(data_dir)
outputfile = os.path.join(result_dir,'sampling-efficiency.json')

results = {}
results['perbench']  =  get_perbench ()
results['overall']   =  get_overall  ()

results['plotinfo'] = {}
results['plotinfo']['perbench'] = { 'filename': 'sampling-efficiency_perbench', 'xlabel': 'Sampling Efficiency' }
results['plotinfo']['overall' ] = { 'filename': 'sampling-efficiency_overall' , 'ylabel': 'Sampling Efficiency', 'xlabel': 'Monitor Type' }

print('.) done with overall')

with open(outputfile,'w') as fd:
    json.dump(results,fd)

print('.) wrote to outfile: ' + outputfile)
