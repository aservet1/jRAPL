#!/usr/bin/env python3

# To add a new cell, type '# %%'
# To add a new markdown cell, type '# %% [markdown]'
# %%
import os
import json
import pandas as pd
from sys import argv

#from tabulate import tabulate


# %%
def json_from_file(filename): # assumes the file is in JSON format
    with open(filename) as f:
        data = json.loads(f.read())
    return data

def no_dupes(l): # abstract ou[ d['metadata']['benchmark'] for d in data ]t the whole weird list/set method of removing duplicates
    return list(set(l))


def diff_tuple(a,b,r=4):
    diff = a-b
    percent_diff = (diff/b) * 100
    return  ( round(float(percent_diff),r) , round(float(diff),r) )

def memory_comparison(memdata):
    jraplon  = memdata['jraplon']
    jraploff = memdata['jraploff']
    res = {}

    """should i be subtracting stdev? its not in the current list, but if i add it as an item of the list then itll work in just fine"""

    for v in ['avg' , 'median_median' , 'average_median' , 'global_min' , 'global_max' , 'avg_min' , 'avg_max' ]:
        tup = diff_tuple(jraplon[v],jraploff[v])
        res[v] = tup #"(%.4f,  %.4f)" % (tup[0],tup[1])
    return res

#def monitor_ranking(data):
#    for v in ['avg' , 'median_median' , 'average_median' , 'global_min' , 'global_max' , 'avg_min' , 'avg_max' ]:
#        #print([ data[monitor_type][v] for monitor_type in data ])
#        for monitor_type in list(set(data)):
#            print(monitor_type)
#            #print(data[monitor_type])
#    return 1111111111


# %%
if len(argv) != 2:
    print("bad usage: include dir with data as arg")
    exit(2)
data_directory = argv[1]
os.chdir(data_directory)


# %%
filenames = [ _ for _ in os.listdir() if _.endswith('.aggregate-stats.json') ]
data = [ json_from_file(filename) for filename in filenames ]
#display(data[0])

# %%
benchmarks = no_dupes([ d['metadata']['benchmark'] for d in data ])
monitor_types = no_dupes([ d['metadata']['monitor_type'] for d in data ])

data_table = {}
#for monitor_type in monitor_types:
#    data_table[monitor_type] = {}
for d in data:
    metadata = d['metadata']
    monitor_type = metadata['monitor_type']
    benchmark = metadata['benchmark']
    if not benchmark in data_table:
        data_table[benchmark] = {}
    if not monitor_type in data_table[benchmark]:
        data_table[benchmark][monitor_type] = {}
    data_table[benchmark][monitor_type] = memory_comparison(d['memory'])
    print(benchmark,monitor_type)
    #data_table[benchmark]['monitor_rank'] = monitor_ranking(data_table[benchmark])


# %%
os.mkdir('latex')
for benchmark in data_table:
    fname = "latex/"+benchmark+"_memory-comparison.tex"
    with open(fname,'w') as fh:
        df = pd.DataFrame.from_dict(data_table[benchmark])
        fh.write(df.to_latex())#tabulate(df, headers='keys',tablefmt='latex') )
        print("wrote "+fname)


# %%



