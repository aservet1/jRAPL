# 3 plots: energy per dram&pkg, time per sample

import json
from sys import argv
import matplotlib.pyplot as plt
from math import sqrt

if len(argv) < 2:
	print('usage: '+argv[0]+'socket1.json ... socketN.json')
	print('  socketI.json must be in socket order')
	exit(2)

title = {
	'dram': 'average dram energy (time normalized)',
	'pkg': 'average pkg energy (time normalized)'
}

persockets = list()
for jsonfile in argv[1:]:
	with open(jsonfile) as f:
		persockets.append(json.loads(f.read()))
num_sockets = len(persockets)

monitor_types = ['c-dynamicarray','c-linklist','java']
power_domains = ['dram','pkg']

for powd in power_domains:
	fig, axs = plt.subplots(num_sockets)
	for i in range(num_sockets):
		socket = i+1
		data = persockets[i]

		time_normalized_energies = [ data[montype][powd]['avg']/data[montype]['timestamp']['avg'] for montype in monitor_types ]
		
		heights = time_normalized_energies
		yerrors = [ \
			sqrt(((data[montype][powd]['avg']/data[montype]['timestamp']['avg'])**2)\
				*\
				(((data[montype][powd]['stdev']/data[montype][powd]['avg'])**2\
					+\
			    (data[montype]['timestamp']['stdev']/data[montype]['timestamp']['avg'])**2 )))\
			for montype in monitor_types ]

		axs[i].bar(range(1,len(monitor_types)+1), heights, yerr=yerrors, tick_label=monitor_types)
		axs[i].set_title(title[powd]+' for socket '+str(socket))
		plt.ylabel('joules per usec')
	
	fig.tight_layout()
	plt.savefig(powd+'_normalized_to_time')
	plt.clf()
