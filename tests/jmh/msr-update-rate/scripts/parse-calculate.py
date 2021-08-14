#!/usr/bin/env python3

import pandas as pd
import statistics
from sys import argv
import numpy as np
import json

### Get durations in between consecutive timestamps rather than the 
def diff_list(l):
    dl = []
    prev = l[0]
    for item in l[1:]:
        dl.append(item-prev)
        prev = item
    return dl

def cumulative_sum(l):
    sl = []
    sum_ = 0
    for duration in l:
        sum_ += duration
        sl.append(sum_)
    return sl

def filter_for_outliers(unfiltered, **kwargs):
    allowed_kws = {"lt", "gt", "ge", "le", "eq"}
    kws_provided = set(kwargs.keys())
    diff = kws_provided - allowed_kws
    if diff:
        raise ValueError(f"Invalid keyword(s) provided: {diff}")
    filtered = []
    outliers = []
    for val in unfiltered:
        append_val = True
        if "lt" in kws_provided:
            append_val &= (val < kwargs["lt"])
        if "gt" in kws_provided:
            append_val &= (val > kwargs["gt"])
        if "ge" in kws_provided:
            append_val &= (val >= kwargs["ge"])
        if "le" in kws_provided:
            append_val &= (val <= kwargs["le"])
        if "eq" in kws_provided:
            append_val &= (val == kwargs["eq"])
        if append_val: filtered.append(int(val))
        else: outliers.append(int(val))
    return filtered, outliers

'''-------------------------------------------------------------------------------------------'''

try:
    datafile = argv[1]
    outfile  = argv[2]
except:
    print("usage",argv[0],"<csv data file> <json output file>")
    exit(2)

datafile = argv[1]
df = pd.read_csv(datafile)
df = df.loc[:, (df != 0).any(axis=0)]
print('read the csv')
header = list(df.head())
del header[-1] # remove timestamp
timestamps = [ int(i) for i in df['timestamp'] ]
print(header)
print(len(df),'total samples')

result_dfs = dict()


for powerDomain in header:
    print(f'started powerDomain={powerDomain}')

    energies = df[powerDomain]
    lastDifferent = energies[0]
    change_ts = [ timestamps[0] ] # timestamps where there was an energy update
    ener_diffs = [] # differences between consecutive non-equal readings
    bw = 0
    for i in range(len(energies)):
        if energies[i] != lastDifferent:
            ener_diffs.append(energies[i] - lastDifferent)
            change_ts.append(timestamps[i])
            lastDifferent = energies[i]
            bw = 0
        else:
            bw += 1
    # Get the difference between consecutive timestamps, convert to milliseconds
    ts_diffs = diff_list(change_ts) # [ usec/1000 for usec in diff_list(change_ts) ]
    
    # Get the filtered durations <=3 and outliers >3
    cutoff = 10000 # 3 ms cutoff
    filtered, outliers = filter_for_outliers(ts_diffs, le=cutoff)
    
    result = filtered
    
    result_dfs[powerDomain] = {
        #"reading_change_num": non_zero_reading_nums,
        #"change_timestamps": change_ts,
        #"energy_differences": ener_diffs,
        "filtered": filtered,
        "outliers": outliers
    }  
    
    print(f'collected result dataframe for powerDomain={powerDomain};\tlen(outliers)={len(outliers)};\tlen(result)={len(result)}')

# percent that were outliers (above 3000 ms)
nfiltered = len(result_dfs['dram_socket1']['filtered']) + len(result_dfs['pkg_socket1']['filtered'])
nexcluded = len(result_dfs['dram_socket1']['outliers']) + len(result_dfs['pkg_socket1']['outliers'])
result_dfs['percent-excluded'] = nexcluded/nfiltered * 100

with open(outfile,'w') as fd:
    fd.write(json.dumps(result_dfs))

dram_mean  = statistics.mean(result_dfs['dram_socket1']['filtered'])
dram_stdev = statistics.mean(result_dfs['dram_socket1']['filtered'])

pkg_mean   = statistics.mean(result_dfs['pkg_socket1']['filtered'])
pkg_stdev  = statistics.stdev(result_dfs['pkg_socket1']['filtered'])

both = list(result_dfs['dram_socket1']['filtered'])
both.extend(list(result_dfs['pkg_socket1']['filtered']))
both_mean  = statistics.mean(both)
both_stdev = statistics.stdev(both)

print("Mean and stdev:")
print(json.dumps({
    'dram_mean':dram_mean,
    'dram_stdev':dram_stdev,
    'pkg_mean':pkg_mean,
    'pkg_stdev':pkg_stdev,
    'both_mean':both_mean, 
    'both_stdev':both_stdev
}, indent=2))


