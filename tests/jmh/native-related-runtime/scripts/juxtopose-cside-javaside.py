#!/usr/bin/env python3
import os
import statistics
from sys import argv
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle

'''-----------------------------------------------'''

def load_hist(filename):
	hist = {}

	data = {}
	n = 0; sum_ = 0
	with open(filename) as fh:
		for line in fh:
			k, v = [ int(i) for i in line.split() ]
			data[k] = v
			n += v; sum_ += k*v

	mean = sum_/n
	sq_sum = 0
	for time in data:
		sq_sum += ((time-mean)**2)*data[time]
	stdev = (sq_sum / n)**0.5

	hist['metadata'] = {}
	hist['metadata']['avg'] = mean
	hist['metadata']['stdev'] = stdev

	hist['data'] = {}
	hist['data']['filtered'] = {}
	hist['data']['outliers'] = {}
	for time in data:
		if time <= mean + 3*stdev or time <= mean - 3*stdev:
			hist['data']['filtered'][time] = data[time]
		else:
			hist['data']['outliers'][time] = data[time]

	return hist

def trunc_str(f):
	return "%.2f" % f

'''-----------------------------------------------'''

try:
	cside = argv[1]
	javaside = argv[2]
	result_dir = argv[3]
except IndexError:
	print("usage:", argv[0], "cside-data javaside-data result-dir")
	exit(2)

c = 'limegreen'
e = 'black'
a = .9

cside = load_hist(cside)
javaside = load_hist(javaside)

fig, axs = plt.subplots(1, 2, figsize=(10,5))

rectangle1 = Rectangle((0, 0), 1, 1, fc="w", fill=False, edgecolor='none', linewidth=0)
rectangle2 = Rectangle((0, 0), 1, 1, fc="w", fill=False, edgecolor='none', linewidth=0)

axs[0].bar ( \
	cside['data']['filtered'].keys(), \
	cside['data']['filtered'].values(), \
	color = c, edgecolor = e, alpha = a
)
axs[0].legend ( \
	[ \
		rectangle1, \
		rectangle2  \
	], \
	(  \
		"x̄: "+trunc_str(cside['metadata'] ['avg'] )+" µsec", \
		"σ: "+trunc_str(cside['metadata']['stdev'])+" µsec"  \
	) \
)
axs[0].set_title("C")

axs[1].bar ( \
	javaside['data']['filtered'].keys(), \
	javaside['data']['filtered'].values(), \
	color = c, edgecolor = e, alpha = a
)
axs[1].legend ( \
	[ \
		rectangle1, \
		rectangle2  \
	], \
	(  \
		"x̄: "+trunc_str(javaside['metadata'] ['avg'] )+" µsec", \
		"σ: "+trunc_str(javaside['metadata']['stdev'])+" µsec"  \
	) \
)
axs[1].set_title("Java")
axs[1].set_yticks(axs[0].get_yticks()) # align y axis idiom

fig.supxlabel('Time (μs)')
fig.supylabel('Frequency')

# plt.show(); exit();
plt.savefig( \
	os.path.join( \
		result_dir, \
		'juxtaposed' \
	) \
)




