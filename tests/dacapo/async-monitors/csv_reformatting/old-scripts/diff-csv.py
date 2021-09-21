#!/usr/bin/env python3

import pandas as pd
from sys import argv

def diff_list(lst, wrap = 0):
    diffs = []
    for i in range(1,len(lst)):
        diff = lst[i] - lst[i-1]
        if diff < 0:
            diff += wrap
        diffs.append(diff)
    return diffs

try:
    infile = argv[1]
    outfile = argv[2]
except:
    print("usage:",argv[0],"infile.csv outfile.csv")
    exit(2)

print(' ~0)) '+infile)

NRML_RAPL_WRAPAROUND = 262143.99993896484
DRAM_RAPL_WRAPAROUND = 64424.509425000004

df = pd.read_csv(infile)
diffdf = pd.DataFrame()

for key in df:
    if key == 'timestamp':
        continue
    if 'dram' in key:
        wrap = DRAM_RAPL_WRAPAROUND
    else:
        wrap = NRML_RAPL_WRAPAROUND

    diffdf[key] = diff_list(df[key],wrap)

diffdf.rename(columns={'timestamp':'time_elapsed'}, inplace = True)
diffdf['start_timestamp'] = df['timestamp'].to_list()[:-1]
diffdf['elapsed_time'] = diff_list(df['timestamp'])

diffdf.to_csv(outfile, index = False)

print(' ~1)) '+infile)
