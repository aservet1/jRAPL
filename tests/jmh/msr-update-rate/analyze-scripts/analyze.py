#!/usr/bin/env python3
import pandas as pd
import statistics
from sys import argv

def diff_list(l): # durations as opposed to cumulative raw timestamps
    dl = list()
    prev = l[0]
    for item in l[1:]:
        dl.append(item-prev)
        prev = item
    return dl

df = pd.read_csv(argv[1])
print('read the csv')

header = list(df.head())
del header[-1] # remove timestamp
timestamps = df['timestamp']

results_df = dict()

for powerDomain in header:
	energies = df[powerDomain]
	lastDifferent = energies[0]
	ts_diffs = [ timestamps[0] ] # timestamps where there was an energy update
	for i in range(len(energies)):
		if energies[i] != lastDifferent:
			ts_diffs.append(timestamps[i])
			lastDifferent = energies[i]

	ts_diffs = diff_list(ts_diffs) # get durations instead of raw timestamps
	
	# removing outliers with 3 standard deviation method
	if len(ts_diffs) >= 2: # make sure there were any changes in energy readings to begin with (core is inactive on Joleton for some reason)
		sd = statistics.stdev(ts_diffs)
		avg = statistics.mean(ts_diffs)

		filtered = list(); outliers = list()

		for tsd in ts_diffs:
			if (tsd > avg + 3*sd) or (tsd < avg - 3*sd):
				outliers.append(tsd)
			else:
				filtered.append(tsd)
	
		results_df[powerDomain] = filtered
	else:
		results_df[powerDomain] = ts_diffs
	
	print(f'collected result dataframe for {powerDomain}')
