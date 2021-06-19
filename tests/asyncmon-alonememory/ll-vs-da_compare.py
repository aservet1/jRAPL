#!/usr/bin/env python3
import json
import os
from sys import argv
import matplotlib.pyplot as plt
import statistics
import math
import pandas as pd

def getName(fname):
	if '/' in fname: fname = fname.split('/')[-1]
	if '.' in fname: fname = fname.split('.')[0]
	return fname

try:
	ll_src = argv[1]
	da_src = argv[2]
except:
	print("usage:",argv[0],"linkedlist-data.json dynamicarray.json")
	exit(2)

print()
fnames = [ll_src, da_src]
parsed = dict()
serieses = []
for idx, fname in enumerate([ll_src, da_src]):
	print(" .|_started processing", getName(fname))
	with open(fname) as f: data = json.load(f)

	parsed[getName(fname)] = dict()

	samples = [ s/1024/1024 for s in data['samples'] ]
	parsed[getName(fname)]['avg'] = statistics.mean(samples)
	parsed[getName(fname)]['std'] = statistics.stdev(samples)
	parsed[getName(fname)]['numSamples'] = len(samples)
	serieses.append( pd.Series(samples) )

	print(" .|_finished processing", getName(fname),"\n")

parsed['diff'] = dict()
parsed['diff']['avg'] = parsed[getName(ll_src)]['avg'] - parsed[getName(da_src)]['avg']
parsed['diff']['std'] = math.sqrt(parsed[getName(ll_src)]['std']**2 + parsed[getName(da_src)]['std']**2)
parsed['diff']['numSamples'] = parsed[getName(ll_src)]['numSamples'] - parsed[getName(da_src)]['numSamples']

parsed['_unit'] = "MB"

print('pcc>>',serieses[0].corr(serieses[1]))

print(json.dumps(parsed,indent=2))
