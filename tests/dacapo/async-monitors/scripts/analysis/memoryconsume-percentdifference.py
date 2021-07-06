#!/usr/bin/env python3

import os
import json
from math import sqrt
from sys import argv

from myutil import parse_cmdline_args

'''---------------------------------------------------------------------------------------------------'''

def percent_diff(a,b):
    return (a-b)/((a+b)/2) * 100

def percent_diff_propagate_uncertainty(sa,sb,a,b): # uses propagation of error through each step of a percent difference
    subtract = a-b
    subtract_sd = sqrt((sa**2)+(sb**2))
    average = (a+b)/2
    average_sd = sqrt(.5**2 * sa**2 + .5**2 * sb**2)
    return sqrt( ((subtract/average)**2) * ( (subtract_sd/subtract)**2 + (average_sd/average)**2 ) * 100 )

'''---------------------------------------------------------------------------------------------------'''

def load_data_by_file_extension(ext, benchmark_or_monitorType):
    files = sorted([ f for f in os.listdir() if f.endswith(ext) ])
    if not len(files):
        print("no files found with ext",ext)
        exit(2)

    data = []
    for fname in files:
        with open(fname) as f:
            data.append(json.loads(f.read()))
    tmp = {}
    for d in data:
        bench = d['metadata'][benchmark_or_monitorType]
        if not bench in tmp.keys(): tmp[bench] = [d]
        else: tmp[bench].append(d)
    data = tmp
    return data

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
        nojavg = get_by_monitor_type(data, monitor_type)['memory']['jraploff']['avg']
        nojstd = get_by_monitor_type(data, monitor_type)['memory']['jraploff']['stdev']

        result[benchmark] = {}

        result[benchmark]['java'] = {}
        result[benchmark]['java']['avg']   = percent_diff(javg, nojavg)
        result[benchmark]['java']['stdev'] = percent_diff_propagate_uncertainty(jstd, nojstd, javg, nojavg)

        result[benchmark]['c-linklist'] = {}
        result[benchmark]['c-linklist']['avg']   = percent_diff(clavg, nojavg)
        result[benchmark]['c-linklist']['stdev'] = percent_diff_propagate_uncertainty(clstd, nojstd, clavg, nojavg)

        result[benchmark]['c-dynamicarray'] = {}
        result[benchmark]['c-dynamicarray']['avg']   = percent_diff(cdavg, nojavg)
        result[benchmark]['c-dynamicarray']['stdev'] = percent_diff_propagate_uncertainty(cdstd, nojstd, cdavg, nojavg)

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
    nojavg = data[monitor_type][0]['memory']['jraploff']['avg']
    nojstd = data[monitor_type][0]['memory']['jraploff']['stdev']

    result = {}
    result['java'] = {}
    result['java']['avg']   = percent_diff(javg, nojavg)
    result['java']['stdev'] = percent_diff_propagate_uncertainty(jstd, nojstd, javg, nojavg)
    result['c-linklist'] = {}
    result['c-linklist']['avg']   = percent_diff(clavg, nojavg)
    result['c-linklist']['stdev'] = percent_diff_propagate_uncertainty(clstd, nojstd, clavg, nojavg)
    result['c-dynamicarray'] = {}
    result['c-dynamicarray']['avg']   = percent_diff(cdavg, nojavg)
    result['c-dynamicarray']['stdev'] = percent_diff_propagate_uncertainty(cdstd, nojstd, cdavg, nojavg)

    return result

'''---------------------------------------------------------------------------------------------------'''

print('.) starting')

data_dir, result_dir = parse_cmdline_args(argv)
outputfile = os.path.join(result_dir,'memory-percent-difference.json')

os.chdir(data_dir)
results = {}
results['perbench'] = get_perbench()
results['overall']  = get_overall()

print('.) done with overall')

with open(outputfile,'w') as fd:
    json.dump(results,fd)

print('.) wrote to outfile: ' + outputfile)

'''---------------------------------------------------------------------------------------------------'''