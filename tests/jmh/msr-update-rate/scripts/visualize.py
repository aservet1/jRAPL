#!/usr/bin/env python3

import os
import json
from sys import argv
import matplotlib.pyplot as plt

from statistics import mean, stdev

try:
    datafile = argv[1]
    result_dir = argv[2]
    system_name = argv[3]
except IndexError:
    print("usage:",argv[0],"<data file> <result dir> <System{A,B,C,...}>")
    exit(2)

with open(datafile) as fh:
    data = json.loads(fh.read())

# we're just picking it out of socket1 for what to display in the research paper
s = 1
dram = data['dram_socket'+str(s)]
pkg = data['pkg_socket'+str(s)]
# core = data['core_socket'+str(s)]
# gpu = data['gpu_socket'+str(s)]

# fig, axs = plt.subplots(1,4,figsize=(15,5))
fig, axs = plt.subplots(1,2,figsize=(15,5))

axs[0].scatter(range(len(dram['filtered'])),dram['filtered'])
axs[0].set_title('dram')
axs[0].set_ylabel('update time (usec)')


axs[1].scatter(range(len(pkg['filtered'])),pkg['filtered'])
axs[1].set_title('pkg')

# axs[2].scatter(range(len(gpu['filtered'])),gpu['filtered'])
# axs[2].set_title('gpu')

# axs[3].scatter(range(len(core['filtered'])),core['filtered'])
# axs[3].set_title('core')

fig.suptitle('Energy Update Time Scatter' + system_name)
fig.align_ylabels(axs)

#plt.show()
os.chdir(result_dir)
fig.savefig('energy-update-time-simple_' + system_name)

print('>>','done with scatterplot')

'''------------------------------------------------------------------------------'''

fig.clf(); plt.clf();

plt.bar ( \
	['dram','pkg'], \
	[mean(dram['filtered']), mean(pkg['filtered'])], \
	yerr=[stdev(dram['filtered']), stdev(pkg['filtered'])] \
)

plt.title('Average Update Time ' + system_name)
plt.ylabel('time (ms)')
plt.savefig('energy-update-time-average_'+system_name)

print('>>','done with average bar plot')


