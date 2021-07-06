#!/usr/bin/env python3
import json
import os
from sys import argv
import matplotlib.pyplot as plt

try:
	src_dir = argv[1]
	dest_dir = argv[2]
except:
	print("usage:",argv[0],"src_dir dest_dir")
	exit(2)

if not dest_dir.startswith("/") and not dest_dir.startswith("~"):
	dest_dir = os.path.join(os.getcwd(),dest_dir)

if not os.path.isdir(dest_dir):
	os.mkdir(dest_dir)

os.chdir(src_dir)

print()
for fname in sorted(os.listdir()):
	if not fname.endswith('.json'): continue

	print(" .|_started", fname)
	with open(fname) as f: data = json.load(f)
	samples = data['samples']
	time = data['timestamps']
	plt.clf()
	x = [ t / 1000 for t in time ]
	y = [ s / 1024 / 1024 for s in samples ]
	plt.plot(x,y,label='memoryUsed')

	samples = data['totalMemorySeries']
	time = data['timestamps']
	x = [ t / 1000 for t in time ]
	y = [ s / 1024 / 1024 for s in samples ]
	plt.plot(x,y,label='totalMemory')

	plt.legend(loc='best')

	name = fname.split('.')[0]
	plt.title(name)
	plt.ylabel("memory (MB)")
	plt.xlabel("time (sec)")
	plt.savefig(os.path.join(dest_dir,name))
	print(" .|_finished", fname,"\n")
