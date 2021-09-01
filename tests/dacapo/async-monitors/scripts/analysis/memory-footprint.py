#!/usr/bin/env python3

import os
import json
from math import sqrt
from sys import argv

from myutil import parse_cmdline_args, load_data_by_file_extension
from aggr_utils import percent_diff, percent_diff_propagate_uncertainty, division_propagate_uncertainty

'''---------------------------------------------------------------------------------------------------'''
def get_perbench():
    data = load_data_by_file_extension('.aggregate-perbench.json', 'benchmark')
    result = {}
    for benchmark in sorted(data.keys()):
        def get_by_monitor_type(data, monitor_type):
            return [ d for d in data[benchmark] if d['metadata']['monitor_type'] == monitor_type ][0]

        javg = get_by_monitor_type(data,'java')['memory']['jraplon']['avg']
        jstd = get_by_monitor_type(data,'java')['memory']['jraplon']['stdev']

        clavg = get_by_monitor_type(data,'c-linklist')['memory']['jraplon']['avg'] 
        clstd = get_by_monitor_type(data,'c-linklist')['memory']['jraplon']['stdev']

        cdavg = get_by_monitor_type(data,'c-dynamicarray')['memory']['jraplon']['avg']  
        cdstd = get_by_monitor_type(data,'c-dynamicarray')['memory']['jraplon']['stdev']

        monitor_type = 'c-dynamicarray' # arbitrary monitor type, since 'jraploff' will be the same for all of them
        nojravg = get_by_monitor_type(data, monitor_type)['memory']['jraploff']['avg']
        nojrstd = get_by_monitor_type(data, monitor_type)['memory']['jraploff']['stdev']

        result[benchmark] = {}

        result[benchmark]['java'] = {}
        result[benchmark]['java']['avg']   = percent_diff(javg, nojravg)
        result[benchmark]['java']['stdev'] = percent_diff_propagate_uncertainty(jstd, nojrstd, javg, nojravg)

        result[benchmark]['c-linklist'] = {}
        result[benchmark]['c-linklist']['avg']   = percent_diff(clavg, nojravg)
        result[benchmark]['c-linklist']['stdev'] = percent_diff_propagate_uncertainty(clstd, nojrstd, clavg, nojravg)

        result[benchmark]['c-dynamicarray'] = {}
        result[benchmark]['c-dynamicarray']['avg']   = percent_diff(cdavg, nojravg)
        result[benchmark]['c-dynamicarray']['stdev'] = percent_diff_propagate_uncertainty(cdstd, nojrstd, cdavg, nojravg)

    return result

def get_overall():
    data = load_data_by_file_extension('.aggregate-permonitor.json', 'monitor_type')
    assert(len(data['java']) == 1 and len(data['c-linklist']) == 1 and len(data['c-dynamicarray']) == 1)

    javg = data['java'][0]['memory']['jraplon']['avg']
    jstd = data['java'][0]['memory']['jraplon']['stdev']

    clavg = data['c-linklist'][0]['memory']['jraplon']['avg'] 
    clstd = data['c-linklist'][0]['memory']['jraplon']['stdev']

    cdavg = data['c-dynamicarray'][0]['memory']['jraplon']['avg']  
    cdstd = data['c-dynamicarray'][0]['memory']['jraplon']['stdev']

    monitor_type = 'c-dynamicarray' # arbitrary monitor type, since 'jraploff' will be the same for all of them
    nojravg = data[monitor_type][0]['memory']['jraploff']['avg']
    nojrstd = data[monitor_type][0]['memory']['jraploff']['stdev']

    result = {}

    result['raws'] = {
        'java': {
            'avg':   { 'on': javg, 'off': nojravg },
            'stdev': { 'on': jstd, 'off': nojrstd }
        },
        'c-linklist': {
            'avg':   { 'on': clavg, 'off': nojravg },
            'stdev': { 'on': clstd, 'off': nojrstd }
        },
        'c-dynamicarray': {
            'avg'  : { 'on': cdavg, 'off': nojravg },
            'stdev': { 'on': cdstd, 'off': nojrstd }
        }
    }

    result['normalized'] = {
        'java': {
            'avg':   { javg / nojravg },
            'stdev': { division_propagate_uncertainty(jstd, nojrstd, javg, nojravg) }
        },
        'c-linklist': {
            'avg':   { clavg / nojravg },
            'stdev': { division_propagate_uncertainty(clstd, nojrstd, clavg, nojravg) }
        },
        'c-dynamicarray': {
            'avg'  : { cdavg / nojravg },
            'stdev': { division_propagate_uncertainty(cdstd, nojrstd, cdavg, nojravg) }
        }
    }

    result['percentdiff'] = {}
    result['percentdiff']['java'] = {}
    result['percentdiff']['java']['avg']   = percent_diff(javg, nojravg)
    result['percentdiff']['java']['stdev'] = percent_diff_propagate_uncertainty(jstd, nojrstd, javg, nojravg)
    result['percentdiff']['c-linklist'] = {}
    result['percentdiff']['c-linklist']['avg']   = percent_diff(clavg, nojravg)
    result['percentdiff']['c-linklist']['stdev'] = percent_diff_propagate_uncertainty(clstd, nojrstd, clavg, nojravg)
    result['percentdiff']['c-dynamicarray'] = {}
    result['percentdiff']['c-dynamicarray']['avg']   = percent_diff(cdavg, nojravg)
    result['percentdiff']['c-dynamicarray']['stdev'] = percent_diff_propagate_uncertainty(cdstd, nojrstd, cdavg, nojravg)

    return result

'''---------------------------------------------------------------------------------------------------'''

print('.) starting')

data_dir, result_dir = parse_cmdline_args(argv)
outputfile = os.path.join(result_dir,'memory-footprint.json')

os.chdir(data_dir)
results = {}
results['perbench'] = get_perbench()
results['overall']  = get_overall()

print('.) done with overall')

with open(outputfile,'w') as fd:
    json.dump(results,fd)

print('.) wrote to outfile: ' + outputfile)

'''---------------------------------------------------------------------------------------------------'''
