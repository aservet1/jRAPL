#!/usr/bin/env python3

import os
import json
import numpy as np
import matplotlib as mpl
import matplotlib.pyplot as plt
from sys import argv

from myutil import parse_cmdline_args, plt_set_axis_limits

'''--------------------------------------------------------------------------------'''
def do_perbench(data):
	labels = []
	java_avg = []; c_da_avg = []; c_ll_avg = [];
	java_std = []; c_da_std = []; c_ll_std = [];

	plotinfo = data['plotinfo']['perbench']

	for benchmark in sorted(data['perbench'].keys()):

		labels.append(benchmark)

		java_avg.append(data['perbench'][benchmark]['java']['avg'])
		java_std.append(data['perbench'][benchmark]['java']['stdev'])

		c_ll_avg.append(data['perbench'][benchmark]['c-linklist']['avg'])
		c_ll_std.append(data['perbench'][benchmark]['c-linklist']['stdev'])

		c_da_avg.append(data['perbench'][benchmark]['c-dynamicarray']['avg'])
		c_da_std.append(data['perbench'][benchmark]['c-dynamicarray']['stdev'])

	## Make the all-benchmarks graph ##
	bar_width = 0.25
	mpl.rcParams['figure.dpi'] = 600
	r1 = np.arange(len(c_ll_avg))
	r2 = [x + bar_width for x in r1]
	r3 = [x + bar_width for x in r2]

	plt.clf()
	plt.barh(r1, c_da_avg, bar_width, xerr=c_da_std, color='#003f5c', edgecolor="white", label='C Dynamic Array') 
	plt.barh(r2, c_ll_avg, bar_width, xerr=c_ll_std, color='#bc5090', edgecolor="white", label='C Linked List')   
	plt.barh(r3, java_avg, bar_width, xerr=java_std, color='#ffa600', edgecolor="white", label='Java')            
	# plt.barh(r1, c_da_avg, bar_width, color='#003f5c', edgecolor="white", label='C Dynamic Array') 
	# plt.barh(r2, c_ll_avg, bar_width, color='#bc5090', edgecolor="white", label='C Linked List')   
	# plt.barh(r3, java_avg, bar_width, color='#ffa600', edgecolor="white", label='Java')            

	plt.ylabel('Benchmark', fontweight='bold')
	plt.xlabel(plotinfo['xlabel'], fontweight='bold')
	plt.yticks([r + bar_width for r in range(len(c_ll_avg))], labels)
	plt.xticks(np.linspace(0,1,11))
	plt.legend()
	fig = plt.gcf()
	fig.set_size_inches(15,25)

	plt.savefig(os.path.join(result_dir, plotinfo['filename']))
	print(" <.> done making the per-benchmark graph")


def do_overall(data):
	plotinfo = data['plotinfo']['overall']

	overall_java_avg = data['overall']['java']['avg'] 
	overall_java_std = data['overall']['java']['stdev']

	overall_c_ll_avg = data['overall']['c-linklist']['avg'] 
	overall_c_ll_std = data['overall']['c-linklist']['stdev']

	overall_c_da_avg = data['overall']['c-dynamicarray']['avg'] 
	overall_c_da_std = data['overall']['c-dynamicarray']['stdev']

	labels = ['java','c-linklist','c-dynamicarray']

	plt.clf()

	xrange = (None, None)
	yrange = (.7, .9)
	xaxis_precision, yaxis_precision = (0, 2)
	plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision)

	plt.bar (
		x           =  [0,1,2],
		height      =  [overall_java_avg, overall_c_ll_avg, overall_c_da_avg],
		yerr        =  [overall_java_std, overall_c_ll_std, overall_c_da_std],
		tick_label  =  labels,
		capsize     =  .5,
		color = 'purple',
		edgecolor = 'black',
		alpha = 1
	)

	plt.xlabel(plotinfo['xlabel'])
	plt.ylabel(plotinfo['ylabel'])

	fig = plt.gcf()
	fig.set_size_inches(5,5)

	plt.savefig(os.path.join(result_dir,plotinfo['filename']))
	print(" <.> done making the overall average graph")

'''-----------------------------------------------------------------------------------'''

data_file, result_dir = parse_cmdline_args(argv)

with open(data_file) as fd:
	data = json.load(fd)

do_overall (data)
do_perbench(data)
