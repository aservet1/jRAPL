#!/usr/bin/env python3

## JUST TO BE CLEAR this will work on a file of SOCKET-SEPARATED csvs of AsyncMonitor dumps ##

import os
import json
import statistics
import pandas as pd
from sys import argv

def diff_list(l):
	d = []
	for i in range(1,len(l)):
		d.append(l[i]-l[i-1])
	return d

if len(argv) != 3:
	print('usage: ' + argv[0] + ' dir_with_csvs socket_number')
	exit(2)

print(' >> warning: this takes forever')

socket = int(argv[2])
os.chdir(argv[1])
files = os.listdir()
montypes = list(set([ f.split('.')[0].split('_')[3] for f in files if f.endswith('.csv') ]))

sockfiles = [ f for f in files if f.startswith('Socket'+str(socket)) ]
sockfiles = sorted(sockfiles)

res = { }
res = dict()
for montype in montypes:
	res[montype] = dict()
	res[montype]['pkg'] = list()
	res[montype]['dram'] = list()
	res[montype]['timestamp'] = list()

for filename in sockfiles:
	print(' >> starting '+filename)

	montype = filename.split('.')[0].split('_')[3]
	df = pd.read_csv(filename)

	a = [ x for x in diff_list(df['pkg'].to_list()) if x >= 0] # I THINK THE ONE OR TWO NEGATIVES I COME ACROSS ARE WRAPAROUND, SO ITS PROLLY OK TO JUST THROW OUT THIS ONE DEFECTIVE SAMPLE
	b =	[ x for x in diff_list(df['dram'].to_list()) if x >= 0]
	c = [ x for x in diff_list(df['timestamp'].to_list()) if x >= 0]

	res[montype]['pkg'].extend(a)
	res[montype]['dram'].extend(b)
	res[montype]['timestamp'].extend(c)

	print(' << donewith '+filename)

print('<<<< aggregating for socket '+str(socket))
for montype in res:
	print(' >> montype: '+montype)
	for metric in res[montype]:
		print('   >> metric: '+metric)
		data = res[montype][metric]
		print([d for d in data if d < 0])
		avg = statistics.mean(data)
		res[montype][metric] = { 'avg' : avg, 'stdev' : statistics.stdev(data, avg) }
print('>>>> aggregated')

print(json.dumps(res))
with open('parsed_Socket'+str(socket)+'.json', 'w') as f: f.write(json.dumps(res))


