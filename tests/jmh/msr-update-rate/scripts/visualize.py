#!/usr/bin/env python3

import os
import json
from sys import argv
import matplotlib.pyplot as plt

from statistics import mean, stdev

try:
    datafile    = argv[1]
    result_dir  = argv[2]
    system_name = argv[3]
except IndexError:
    print("usage:",argv[0],"<data file> <result dir> <System{A,B,C,...}>")
    exit(2)

with open(datafile) as fh:
    data = json.loads(fh.read())

# we're just picking it out of socket1 for what to display in the paper
s = 1
dram = data['dram_socket'+str(s)]
pkg = data['pkg_socket'+str(s)]
# core = data['core_socket'+str(s)]
# gpu = data['gpu_socket'+str(s)]

# fig, axs = plt.subplots(1,4,figsize=(15,5))
fig, axs = plt.subplots(1,2,figsize=(15,7.5))

c = 'black'; a = .7

axs[0].scatter    (range(len(dram['filtered'])), dram['filtered'], edgecolor=c, alpha=a)
axs[0].set_title  ('dram')
axs[0].set_ylabel ('update time (μs)')
axs[0].set_xticks([])
axs[0].set_yticks(axs[0].get_yticks()[1:-1])

axs[1].scatter    (range(len(pkg['filtered'])),  pkg['filtered'], edgecolor=c, alpha=a)
axs[1].set_title  ('pkg')
axs[1].set_xticks([])
axs[1].set_yticks(axs[0].get_yticks())

#plt.show()
os.chdir(result_dir)
fig.savefig('energy-update-time_' + system_name)

print('>>','done with scatterplot')

exit(0)
'''------------------------------------------------------------------------------'''

# This below figure is unnecessary, consider deleting it

fig.clf(); plt.clf();

plt.bar (                                                    \
	['dram','pkg'],                                          \
	[mean(dram['filtered']), mean(pkg['filtered'])],         \
	yerr=[stdev(dram['filtered']), stdev(pkg['filtered'])]   \
)                                                                    
                                                                     
# plt.title  ( 'Average Update Time ' + system_name )                  
plt.ylabel (             'time (ms)'              )                  
plt.show   (                                      )                  
# plt.savefig('energy-update-time-average_'+system_name)             

print('>>','done with average bar plot')

