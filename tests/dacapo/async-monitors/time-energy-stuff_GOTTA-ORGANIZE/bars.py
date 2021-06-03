# 3 plots: energy per dram&pkg, time per sample

import json
from sys import argv
import matplotlib.pyplot as plt

if len(argv) < 2:
	print('usage: '+argv[0]+'socket1.json ... socketN.json')
	print('  socketI.json must be in socket order')
	exit(2)

title = {
	'dram': 'average dram energy per 1ms sample',
	'pkg': 'average pkg energy per 1ms sample',
	'timestamp': 'average time per 1ms sample'
}
units = {
	'dram': 'joules',
	'pkg': 'joules',
	'timestamp': 'usec'
}

persockets = list()
for jsonfile in argv[1:]:
	with open(jsonfile) as f:
		persockets.append(json.loads(f.read()))
num_sockets = len(persockets)

monitor_types = ['c-dynamicarray','c-linklist','java']
metrics = ['dram','pkg','timestamp']

for metric in metrics:
	fig, axs = plt.subplots(num_sockets)
	for i in range(num_sockets):
		socket = i+1
		data = persockets[i]
		heights = [ data[montype][metric]['avg'] for montype in monitor_types ]
		yerrors = [ data[montype][metric]['stdev'] for montype in monitor_types ]
		axs[i].bar(range(1,len(monitor_types)+1), heights, yerr=yerrors, tick_label=monitor_types)
		axs[i].set_title(title[metric]+' for socket '+str(socket))
		plt.ylabel(units[metric])
	
	fig.tight_layout()
	plt.savefig(metric+'_average_'+units[metric]+'_per-sample')
	plt.clf()
