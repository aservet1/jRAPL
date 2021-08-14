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
	result_dir = argv[1]
	cside_sysA = argv[2]
	jside_sysA = argv[3]
	cside_sysB = argv[4]
	jside_sysB = argv[5]
except IndexError:
	print("usage:", argv[0], "result_dir cside-systemA javaside-systemA cside-systemB javaside-systemB")#<list of files in order, ie 'cside-systemA javaside-systemA cside-systemB javaside-systemB'> ")
	exit(2)

c = 'limegreen'
e = 'black'
a = .9

rectangle1 = Rectangle((0, 0), 1, 1, fc="w", fill=False, edgecolor='none', linewidth=0)
rectangle2 = Rectangle((0, 0), 1, 1, fc="w", fill=False, edgecolor='none', linewidth=0)

cside_sysA = load_hist(cside_sysA)
jside_sysA = load_hist(jside_sysA)
cside_sysB = load_hist(cside_sysB)
jside_sysB = load_hist(jside_sysB)

fig = plt.figure(constrained_layout=True)
subfigs = fig.subfigures (
	nrows=2,
	ncols=1
)

subfigs[0].suptitle('System A')
axs = subfigs[0].subplots(nrows=1, ncols=2, sharex=True, sharey=True)
axs[0].bar (
	cside_sysA['data']['filtered'].keys(),
	cside_sysA['data']['filtered'].values(),
	color = c, edgecolor = e, alpha = a
)
axs[0].legend ( \
	[ \
		rectangle1, \
		rectangle2  \
	], \
	(  \
		"x̄: "+trunc_str(cside_sysA['metadata'] ['avg'] )+" µsec", \
		"σ: "+trunc_str(cside_sysA['metadata']['stdev'])+" µsec"  \
	) \
)
axs[0].set_title("C")
axs[1].bar (
	jside_sysA['data']['filtered'].keys(),
	jside_sysA['data']['filtered'].values(),
	color = c, edgecolor = e, alpha = a
)
axs[1].legend ( \
	[ \
		rectangle1, \
		rectangle2  \
	], \
	(  \
		"x̄: "+trunc_str(jside_sysA['metadata'] ['avg'] )+" µsec", \
		"σ: "+trunc_str(jside_sysA['metadata']['stdev'])+" µsec"  \
	) \
)
axs[1].set_title("Java")



subfigs[1].suptitle('System B')
axs = subfigs[1].subplots(nrows=1, ncols=2, sharex=True, sharey=True)
axs[0].bar (
	cside_sysB['data']['filtered'].keys(),
	cside_sysB['data']['filtered'].values(),
	color = c, edgecolor = e, alpha = a
)
axs[0].legend ( \
	[ \
		rectangle1, \
		rectangle2  \
	], \
	(  \
		"x̄: "+trunc_str(cside_sysB['metadata'] ['avg'] )+" µsec", \
		"σ: "+trunc_str(cside_sysB['metadata']['stdev'])+" µsec"  \
	) \
)
axs[0].set_title("C")
axs[1].bar (
	jside_sysB['data']['filtered'].keys(),
	jside_sysB['data']['filtered'].values(),
	color = c, edgecolor = e, alpha = a
)
axs[1].legend ( \
	[ \
		rectangle1, \
		rectangle2  \
	], \
	(  \
		"x̄: "+trunc_str(jside_sysB['metadata'] ['avg'] )+" µsec", \
		"σ: "+trunc_str(jside_sysB['metadata']['stdev'])+" µsec"  \
	) \
)
axs[1].set_title("Java")

fig.supxlabel('Run Time (usec)')
fig.supylabel('Frequency')

plt.savefig(
	os.path.join(
		result_dir,
		'juxtaposed_compound'
	)
)
#
# fig, axs = plt.subplots(1, 2, figsize=(10,5))
#
# rectangle1 = Rectangle((0, 0), 1, 1, fc="w", fill=False, edgecolor='none', linewidth=0)
# rectangle2 = Rectangle((0, 0), 1, 1, fc="w", fill=False, edgecolor='none', linewidth=0)
#
# axs[0].bar (
# 	cside['data']['filtered'].keys(),
# 	cside['data']['filtered'].values(),
# 	color = c, edgecolor = e, alpha = a
# )
# axs[0].legend (
# 	[
# 		rectangle1,
# 		rectangle2
# 	],
# 	(
# 		"x̄: "+trunc_str(cside['metadata'] ['avg'] )+" µsec",
# 		"σ: "+trunc_str(cside['metadata']['stdev'])+" µsec"
# 	)
# )
# axs[0].set_title("C")
#
# axs[1].bar (
# 	javaside['data']['filtered'].keys(),
# 	javaside['data']['filtered'].values(),
# 	color = c, edgecolor = e, alpha = a
# )
# axs[1].legend (
# 	[
# 		rectangle1,
# 		rectangle2
# 	],
# 	(
# 		"x̄: "+trunc_str(javaside['metadata'] ['avg'] )+" µsec",
# 		"σ: "+trunc_str(javaside['metadata']['stdev'])+" µsec"
# 	)
# )
# axs[1].set_title("Java")
# axs[1].set_yticks(axs[0].get_yticks()) # align y axis idiom
#
# fig.supxlabel('Time (μs)')
# fig.supylabel('Frequency')
#
# # plt.show(); exit();




