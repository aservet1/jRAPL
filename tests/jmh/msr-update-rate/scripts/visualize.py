#!/usr/bin/env python3

import json
from sys import argv
import matplotlib.pyplot as plt

try:
    datafile = argv[1]
    system_name = argv[2]
except IndexError:
    print("usage:",argv[0],"<data file> <System{A,B,C,...}>")
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

fig.suptitle('Energy Update Time ' + system_name)
fig.align_ylabels(axs)

plt.show()
#fig.savefig('energy-update-time-simple_' + system_name)
