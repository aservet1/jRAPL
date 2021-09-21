#!/usr/bin/env python3

import os
import pandas as pd
from sys import argv
from multiprocessing import Pool

class MultiprocessEngine(object):

    def __init__(self, srcdir, dstdir):
        self.srcdir = srcdir
        self.dstdir = dstdir

    def __call__(self, filename):
        
        print(' ~0)) '+filename)

        NRML_RAPL_WRAPAROUND = 262143.99993896484
        DRAM_RAPL_WRAPAROUND = 64424.509425000004 # while this wraparound only applies to DRAM on broadwell machines, i can broadly apply it as the general DRAM wraparound because from previous investigation i already know that for this dataset there were only incidents of dram wraparound on the broadwell machine (vaporeon)

        df = pd.read_csv(os.path.join(srcdir,filename))
        diffdf = pd.DataFrame()

        for key in df:
            if key == 'timestamp': continue
            if 'dram' in key: wrap = DRAM_RAPL_WRAPAROUND
            else: wrap = NRML_RAPL_WRAPAROUND
            diffdf[key] = diff_list(df[key],wrap)

        diffdf.rename(columns={'timestamp':'time_elapsed'}, inplace = True)
        diffdf['start_timestamp'] = df['timestamp'].to_list()[:-1]
        diffdf['elapsed_time'] = diff_list(df['timestamp'])

        diffdf.to_csv(os.path.join(dstdir,filename), index = False)

        print(' ~1)) '+filename)
 
def diff_list(self, lst, wrap = 0):
    diffs = []
    for i in range(1,len(lst)):
        diff = lst[i] - lst[i-1]
        if diff < 0:
            diff += wrap
        diffs.append(diff)
    return diffs

try:
    srcdir = argv[1]
    dstdir = argv[2]
except:
    print("usage:",argv[0],"srcdir dstdir")
    exit(2)

if not os.path.exists(dstdir):
    os.mkdir(dstdir)

csv_files = sorted([ fname for fname in os.listdir(srcdir) if fname.endswith('.csv') ])

try:
    pool = Pool(os.cpu_count())
    engine = MultiprocessEngine(srcdir, dstdir)
    outputs = pool.map(engine, csv_files)
finally:
    pool.close()
    pool.join()
