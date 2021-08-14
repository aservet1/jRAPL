#!/usr/bin/env python3

import os
import json
from sys import argv
import matplotlib.pyplot as plt

'''-----------------------------------------------------------------------'''
def plot_subfig_row(subfig, datafile):
	with open(datafile) as fh:
		data = json.loads(fh.read())
	
	# we're just picking it out of socket1 for what to display in the paper
	s = 1
	dram = data['dram_socket'+str(s)]
	pkg = data['pkg_socket'+str(s)]
	
	# fig, axs = plt.subplots(1,4,figsize=(15,5))
	axs = subfig.subplots(1,2, sharey=True)
	
	c = 'darkblue'; a = .7
	
	axs[0].scatter	(range(len(dram['filtered'])), dram['filtered'], edgecolor=c, alpha=a)
	axs[0].set_title  ('DRAM')
	axs[0].set_xticks([])
	
	axs[1].scatter	(range(len(pkg['filtered'])),  pkg['filtered'], edgecolor=c, alpha=a)
	axs[1].set_title  ('Package')
	axs[1].set_xticks([])
	
'''-----------------------------------------------------------------------'''

try:
	result_dir  = argv[1]
	sysA_data = argv[2]
	sysB_data = argv[3]
except IndexError:
	print("usage:",argv[0],"<result_dir> <System A data file> <System B data file>")
	exit(2)

fig = plt.figure(constrained_layout=True)
subfigs = fig.subfigures (
	nrows=2,
	ncols=1
)

subfigs[0].suptitle('System A')
plot_subfig_row(subfigs[0],sysA_data)

subfigs[1].suptitle('System B')
plot_subfig_row(subfigs[1],sysB_data)

os.chdir(result_dir)
fig.savefig('energy-update-time_compound')

print('>>','done with scatterplot')

